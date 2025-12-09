package jackiecrazy.footwork.utils;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Matrix3d;
import org.joml.AxisAngle4d;
import org.joml.Vector4d;

/**
 * Utilities to convert (direction + axial roll) Vector4d <-> quaternion and slerp safely.
 *
 * Vector4d format assumed: (dir.x, dir.y, dir.z, axialRollDegrees)
 * where dir is a direction vector in the provided basis (not necessarily normalized).
 *
 * basisForward and basisUp are world-space vectors forming the owner's basis.
 * basisRight will be computed as forward cross up.
 *
 * ChatGPT wrote this.
 */
public final class DirAxialQuat {

    private DirAxialQuat() {}

    // --- Construct a quaternion from direction vector + axial roll (degrees) ---
    public static Quaterniond quatFromDirAxial(Vector4d dirAxial, Vector3d basisForward, Vector3d basisUp) {
        // Normalize direction
        Vector3d localDir = new Vector3d(dirAxial.x, dirAxial.y, dirAxial.z);
        double len = localDir.length();
        if (len < 1e-9) {
            // fallback to forward
            localDir.set(basisForward);
        } else {
            localDir.mul(1.0 / len);
        }

        // Convert localDir (expressed in basisForward/basisRight/basisUp coordinates) into world-space direction.
        // We need basisRight = basisForward x basisUp, then reconstruct worldDir = right*localDir.x + up*localDir.y + forward*localDir.z
        Vector3d forward = new Vector3d(basisForward).normalize();
        Vector3d up = new Vector3d(basisUp).normalize();
        Vector3d right = forward.cross(up, new Vector3d()).normalize();
        // If right is degenerate (forward parallel to up) fallback:
        if (right.lengthSquared() < 1e-9) {
            // choose world X as right then recompute up
            right.set(1, 0, 0);
            up = right.cross(forward, new Vector3d()).normalize();
            right = forward.cross(up, new Vector3d()).normalize();
        }

        Vector3d worldDir = new Vector3d();
        worldDir.fma(localDir.x, right);
        worldDir.fma(localDir.y, up);
        worldDir.fma(localDir.z, forward);
        worldDir.normalize();

        // Build look quaternion that maps model-forward (0,0,1) -> worldDir with up alignment to 'up'
        Quaterniond qLook = lookRotation(worldDir, up);

        // Apply axial roll rotation about forward axis (worldDir) by roll degrees
        double rollRad = Math.toRadians(dirAxial.w);
        Quaterniond qRoll = new Quaterniond();
        qRoll.setAngleAxis(rollRad, worldDir.x, worldDir.y, worldDir.z);

        // Total orientation = qLook * qRoll (apply roll in local-forward space)
        Quaterniond out = new Quaterniond(qLook).mul(qRoll);
        out.normalize();
        return out;
    }

    // --- Slerp between two dir+axial Vector4d by converting to quaternions and slerping ---
    public static Quaterniond slerpDirAxial(Vector4d a, Vector4d b, double t, Vector3d basisForward, Vector3d basisUp) {
        Quaterniond qa = quatFromDirAxial(a, basisForward, basisUp);
        Quaterniond qb = quatFromDirAxial(b, basisForward, basisUp);

        // Ensure shortest path: if dot < 0, negate target quaternion
        double dot = qa.dot(qb);
        if (dot < 0.0) {
            qb.invert(); // qb = -qb (represents same rotation but flips hemisphere)
            dot = -dot;
        }

        // Slerp robustly (JOML has slerp in place)
        Quaterniond out = new Quaterniond();
        qa.slerp(qb, t, out);
        out.normalize();
        return out;
    }

    // --- Convert a quaternion back to Vector4d(dir.x, dir.y, dir.z, rollDegrees) relative to the same basis ---
    public static Vector4d dirAxialFromQuat(Quaterniond q, Vector3d basisForward, Vector3d basisUp) {
        // Rebuild forward/right/up basis in world-space
        Vector3d forward = new Vector3d(basisForward).normalize();
        Vector3d up = new Vector3d(basisUp).normalize();
        Vector3d right = forward.cross(up, new Vector3d()).normalize();
        if (right.lengthSquared() < 1e-9) {
            right.set(1, 0, 0);
            up = right.cross(forward, new Vector3d()).normalize();
            right = forward.cross(up, new Vector3d()).normalize();
        }

        // Extract world-space forward direction of the quaternion (rotating model's local forward (0,0,1))
        // worldDir = q * (0,0,1) * q^-1
        Vector3d modelForward = new Vector3d(0, 0, 1);
        Vector3d worldDir = rotateVectorByQuat(modelForward, q);

        // Express worldDir in local basis coords (x along right, y along up, z along forward)
        double localX = right.dot(worldDir);
        double localY = up.dot(worldDir);
        double localZ = forward.dot(worldDir);

        // Normalize local dir
        double len = Math.sqrt(localX*localX + localY*localY + localZ*localZ);
        if (len < 1e-9) {
            localX = 0; localY = 0; localZ = 1;
        } else {
            localX /= len; localY /= len; localZ /= len;
        }

        // Extract roll angle around forward axis.
        // Compute the world-space 'up' vector after rotation applied to model-up (0,1,0).
        Vector3d modelUp = new Vector3d(0, 1, 0);
        Vector3d worldUpRot = rotateVectorByQuat(modelUp, q);

        // Project worldUpRot onto plane perpendicular to worldDir to find roll
        Vector3d proj = new Vector3d(worldUpRot);
        // remove component along worldDir
        double along = proj.dot(worldDir);
        proj.fma(-along, worldDir); // proj = worldUpRot - along*worldDir
        proj.normalize();

        // Compute reference 'zero-roll' direction in world space:
        // zeroRoll = rotate basisUp to be perpendicular to worldDir, using basisRight/basisUp/basisForward basis.
        Vector3d worldForward = worldDir; // axis
        // Compute basis's up projected onto plane:
        Vector3d basisUpProj = new Vector3d(basisUp);
        double bAlong = basisUpProj.dot(worldForward);
        basisUpProj.fma(-bAlong, worldForward);
        if (basisUpProj.lengthSquared() < 1e-9) {
            // fallback: pick global Y projected
            basisUpProj.set(0,1,0);
            double ba = basisUpProj.dot(worldForward);
            basisUpProj.fma(-ba, worldForward).normalize();
        } else basisUpProj.normalize();

        // Angle between basisUpProj (zero-roll) and proj is the roll
        double cos = Math.max(-1.0, Math.min(1.0, basisUpProj.dot(proj)));
        double rollRad = Math.acos(cos);
        // sign: determine whether proj is to the right or left of basisUpProj around worldDir
        Vector3d cross = basisUpProj.cross(proj, new Vector3d());
        double sign = Math.signum(worldForward.dot(cross));
        if (Double.isNaN(sign) || sign == 0.0) sign = 1.0;
        rollRad *= sign;

        double rollDeg = Math.toDegrees(rollRad);

        return new Vector4d(localX, localY, localZ, rollDeg);
    }

    // ---- small helpers ----

    // Build a look rotation quaternion that aligns model-forward (0,0,1) to 'dir' and orients 'upHint' as up as possible.
    private static Quaterniond lookRotation(Vector3d dir, Vector3d upHint) {
        Vector3d f = new Vector3d(dir).normalize();
        Vector3d up = new Vector3d(upHint).normalize();

        Vector3d r = f.cross(up, new Vector3d()).normalize();
        if (r.lengthSquared() < 1e-9) {
            // degenerate: choose any orthogonal right
            if (Math.abs(f.y) < 0.99) {
                r.set(0,1,0).cross(f, r).normalize();
            } else {
                r.set(1,0,0).cross(f, r).normalize();
            }
        }
        Vector3d u = r.cross(f, new Vector3d()).normalize();

        // rotation matrix columns = [ r | u | f ] mapping model axes to world axes
        // Convert matrix to quaternion
        Matrix3d m = new Matrix3d(
            r.x, u.x, f.x,
            r.y, u.y, f.y,
            r.z, u.z, f.z
        );

        Quaterniond q = new Quaterniond();
        q.setFromNormalized(m);
        q.normalize();
        return q;
    }

    private static Vector3d rotateVectorByQuat(Vector3d v, Quaterniond q) {
        // Using q * v * q^-1
        Quaterniond tmp = new Quaterniond(q);
        tmp.normalize(); // ensure normalized to be safe
        // compute q * (v_as_quat) * q^-1, but we'll do vector-rotation shortcut
        // Use JOML's transform:
        Vector3d out = new Vector3d();
        q.transform(v, out);
        return out;
    }
}
