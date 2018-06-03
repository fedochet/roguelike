package ru.spbau.mit.roguelike.world

data class Line(val points: List<Point>): Iterable<Point> by points {

    companion object {
        fun create(x0: Int, y0: Int, x1: Int, y1: Int): Line {
            val points: MutableList<Point> = mutableListOf()

            var currX = x0
            var currY = y0

            val dx = Math.abs(x1 - currX)
            val dy = Math.abs(y1 - currY)
            val sx = if (currX < x1) 1 else -1
            val sy = if (currY < y1) 1 else -1
            var err = dx - dy

            while (true) {
                points.add(Point(currX, currY, 0))

                if (currX == x1 && currY == y1)
                    break

                val e2 = err * 2
                if (e2 > -dx) {
                    err -= dy
                    currX += sx
                }
                if (e2 < dx) {
                    err += dx
                    currY += sy
                }
            }

            return Line(points)
        }
    }

}