package org.example.app.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import org.example.app.data.RadarPoint
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max

/**
 * Simple OpenGL ES 2.0 renderer that draws radar points as GL_POINTS.
 * Provides updatePoints() to refresh VBO data.
 */
// PUBLIC_INTERFACE
class Plot3DRenderer : GLSurfaceView.Renderer {
    private var program: Int = 0
    private var vbo: IntArray = intArrayOf(0)
    private var pointCount: Int = 0
    private var pointSize = 6f

    private val mvpMatrix = FloatArray(16)
    private val projection = FloatArray(16)
    private val view = FloatArray(16)
    private val model = FloatArray(16)

    private var zoom = 3.5f
    private var angleY = -30f
    private var angleX = 20f

    // Vertex shader: position in xyz and color fixed for now
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec3 aPosition;
        void main() {
            gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
            gl_PointSize = 6.0;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
            gl_FragColor = vec4(0.15, 0.39, 0.92, 0.9);
        }
    """.trimIndent()

    fun updatePoints(points: List<RadarPoint>) {
        pointCount = points.size
        val floats = FloatArray(pointCount * 3)
        var idx = 0
        points.forEach { p ->
            floats[idx++] = p.x / 10f
            floats[idx++] = p.y / 10f
            floats[idx++] = p.z / 10f
        }
        val fb: FloatBuffer = ByteBuffer.allocateDirect(floats.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(floats)
        fb.position(0)

        if (vbo[0] == 0) {
            val tmp = IntArray(1)
            GLES20.glGenBuffers(1, tmp, 0)
            vbo = tmp
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floats.size * 4, fb, GLES20.GL_DYNAMIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.98f, 0.99f, 1f, 1f)
        program = createProgram(vertexShaderCode, fragmentShaderCode)
        Matrix.setIdentityM(model, 0)
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height
        Matrix.frustumM(projection, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
        updateViewMatrix()
        updateMvp()
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        GLES20.glUseProgram(program)
        val uMvp = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(uMvp, 1, false, mvpMatrix, 0)

        val aPos = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 3 * 4, 0)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, pointCount)

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    private fun updateViewMatrix() {
        val eyeX = (zoom * Math.cos(Math.toRadians(angleY.toDouble()))).toFloat()
        val eyeZ = (zoom * Math.sin(Math.toRadians(angleY.toDouble()))).toFloat()
        val eyeY = (zoom * Math.sin(Math.toRadians(angleX.toDouble()))).toFloat()
        Matrix.setLookAtM(view, 0, eyeX, eyeY, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    private fun updateMvp() {
        val vm = FloatArray(16)
        Matrix.multiplyMM(vm, 0, view, 0, model, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projection, 0, vm, 0)
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun createProgram(vs: String, fs: String): Int {
        val v = loadShader(GLES20.GL_VERTEX_SHADER, vs)
        val f = loadShader(GLES20.GL_FRAGMENT_SHADER, fs)
        val p = GLES20.glCreateProgram()
        GLES20.glAttachShader(p, v)
        GLES20.glAttachShader(p, f)
        GLES20.glLinkProgram(p)
        return p
    }
}
