package com.zzx.mylibrary.graphics

class Point3DF {
    var x = 0f
    var y = 0f
    var z = 0f

    constructor() {}
    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(p: Point3DF) {
        x = p.x
        y = p.y
        z = p.z
    }

    /**
     * Set the point's x and y coordinates
     */
    operator fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Set the point's x and y coordinates to the coordinates of p
     */
    fun set(p: Point3DF) {
        x = p.x
        y = p.y
        z = p.z
    }

    fun negate() {
        x = -x
        y = -y
        z = -z
    }

    fun offset(dx: Float, dy: Float, dz: Float) {
        x += dx
        y += dy
        z += dz
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    fun equals(x: Float, y: Float, z: Float): Boolean {
        return this.x == x && this.y == y && this.z == z
    }

    override fun toString(): String {
        return "Point3DF($x, $y, $z)"
    }

    fun getDistance(p: Point3DF): Double {
        return Math.abs(
            Math.sqrt(
                Math.pow(
                    (x - p.x).toDouble(),
                    2.0
                ) + Math.pow((y - p.y).toDouble(), 2.0) + Math.pow((z - p.z).toDouble(), 2.0)
            )
        )
    }
}