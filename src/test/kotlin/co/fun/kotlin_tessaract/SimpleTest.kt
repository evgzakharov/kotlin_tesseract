package co.`fun`.kotlin_tessaract

import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode
import net.sourceforge.tess4j.Tesseract
import org.junit.jupiter.api.Test
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis


class SimpleTest {
    @Test
    fun `full test`() {
        nu.pattern.OpenCV.loadLocally()

        val image = ImageIO.read(URL("http://img.ifcdn.com/images/b313c1f095336b6d681f75888f8932fc8a531eacd4bc436e4d4aeff7b599b600_1.jpg"))
        val pixels = (image.raster.dataBuffer as DataBufferByte).data
        val mat = Mat(image.height, image.width, CvType.CV_8UC3)
            .apply { put(0, 0, pixels) }

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(mat, mat, 244.0, 255.0, Imgproc.THRESH_BINARY)
        Core.bitwise_not(mat, mat)

        val preparedImage = toBufferedImage(mat)

        val libPath = "/usr/local/lib"
        val libTess = File(libPath, "libtesseract.dylib")
        if (libTess.exists()) {
            val jnaLibPath = System.getProperty("jna.library.path")
            if (jnaLibPath == null) {
                System.setProperty("jna.library.path", libPath)
            } else {
                System.setProperty("jna.library.path", libPath + File.pathSeparator + jnaLibPath)
            }
        }

        val api = Tesseract()
        api.setDatapath("/usr/local/share/tessdata/")
        api.setLanguage("eng")

        val time = measureTimeMillis {
            val result = api.getWords(preparedImage, ITessAPI.TessPageIteratorLevel.RIL_WORD)
            println(result)
        }
        println(time)
    }

    @Test
    fun `simple gray image with opencv`() {
        nu.pattern.OpenCV.loadLocally()

        val imagePath =
            "/Users/evgenyzakharov/Workspace/meme-text-recognizer/pictures/8d3066f5e95547e43372b1a1aeb5a7a935211c1f13ee3c74be7788e07dc35926_1.jpg"
        val image = ImageIO.read(File(imagePath))
        val pixels = (image.raster.dataBuffer as DataBufferByte).data
        val mat = Mat(image.height, image.width, CvType.CV_8UC3)
            .apply { put(0, 0, pixels) }

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
    }

    private fun toBufferedImage(mat: Mat): BufferedImage {
        var type = BufferedImage.TYPE_BYTE_GRAY
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR
        }
        val bufferSize = mat.channels() * mat.cols() * mat.rows()
        val b = ByteArray(bufferSize)
        mat[0, 0, b] // get all the pixels
        val image = BufferedImage(mat.cols(), mat.rows(), type)
        val targetPixels = (image.raster.dataBuffer as DataBufferByte).data
        System.arraycopy(b, 0, targetPixels, 0, b.size)
        return image
    }

    @Test
    fun `simple test with tesseract`() {
        val libPath = "/usr/local/lib"
        val libTess = File(libPath, "libtesseract.dylib")
        if (libTess.exists()) {
            val jnaLibPath = System.getProperty("jna.library.path")
            if (jnaLibPath == null) {
                System.setProperty("jna.library.path", libPath)
            } else {
                System.setProperty("jna.library.path", libPath + File.pathSeparator + jnaLibPath)
            }
        }

        val api = Tesseract()
        api.setDatapath("/usr/local/share/tessdata/")
        api.setLanguage("eng")

        val image = ImageIO.read(File("input_file.jpg"))
        val result: String = api.doOCR(image)

        println(result)
    }
}
