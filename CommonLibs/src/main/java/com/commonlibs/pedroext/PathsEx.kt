package com.commonlibs.pedroext

import com.commonlibs.units.Distance2d
import com.commonlibs.units.PoseEx
import com.pedropathing.api.Paths
import com.pedropathing.math.Pose
import com.pedropathing.math.Vector2D
import com.pedropathing.paths.Path
import com.pedropathing.paths.curves.Curve

object PathsEx {
    /**
     * Creates a path from multiple paths.
     */
    fun path(vararg paths: Path): Path {
        return Paths.path(*paths)
    }

    /**
     * Creates a path from a curve.
     */
    fun path(curve: Curve): Path {
        return Paths.path(curve)
    }

    /**
     * Creates a straight line path from the start point to the end point.
     */
    fun line(start: Distance2d, end: Distance2d): Path {
        return Paths.line(start.pedro, end.pedro)
    }

    /**
     * Creates a straight line path from the start point to the end point.
     */
    fun line(start: Vector2D, end: Vector2D): Path {
        return Paths.line(start, end)
    }

    /**
     * Creates a straight line path from the start pose to the end pose.
     */
    fun line(start: PoseEx, end: PoseEx): Path {
        return Paths.line(start.pedro, end.pedro)
    }

    /**
     * Creates a straight line path from the start pose to the end pose.
     */
    fun line(start: Pose, end: Pose): Path {
        return Paths.line(start, end)
    }

    /**
     * Creates a Bézier curve.
     * Requires at least 2 control poses.
     * The first and last poses are the start and end of the curve, while the intermediate poses are control poses
     */
    fun curve(vararg poses: PoseEx): Path {
        return Paths.curve(*poses.map { it.pedro }.toTypedArray())
    }

    /**
     * Creates a Bézier curve.
     * Requires at least 2 control poses.
     * The first and last poses are the start and end of the curve, while the intermediate poses are control poses
     */
    fun curve(vararg poses: Pose): Path {
        return Paths.curve(*poses)
    }

    /**
     * Creates a Bézier curve.
     * Requires at least 2 control points.
     * The first and last points are the start and end of the curve, while the intermediate points are control points
     */
    fun curve(vararg points: Distance2d): Path {
        return Paths.curve(*points.map { it.pedro }.toTypedArray())
    }

    /**
     * Creates a Bézier curve.
     * Requires at least 2 control points.
     * The first and last points are the start and end of the curve, while the intermediate points are control points
     */
    fun curve(vararg points: Vector2D): Path {
        return Paths.curve(*points)
    }

    /**
     * Generates a path with a Bézier curve through the input poses.
     * Requires at least 2 poses.
     */
    fun through(vararg poses: PoseEx): Path {
        return Paths.through(*poses.map { it.pedro }.toTypedArray())
    }

    /**
     * Generates a path with a Bézier curve through the input poses.
     * Requires at least 2 poses.
     */
    fun through(vararg poses: Pose): Path {
        return Paths.through(*poses)
    }
}