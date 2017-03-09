package com.hello.zhifu.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hello.zhifu.utils.QRCodeUtil;

@Controller
public class ImageController {

	private final static String BASE = "";

	@RequestMapping(value = "qrcode")
	public static void todownpic(HttpServletRequest request,
			HttpServletResponse response, String qrcodeurl, String qrcodeId)
			throws IOException {
		if (qrcodeurl != null && !"".equals(qrcodeurl)) {
			BufferedImage ImageTwo = null;
			try {
				// 背景
				File fileOne = new File("C:\\Program Files\\apache-tomcat-7.0\\webapps\\zhifu\\assets\\style\\images\\timg.jpg");
				BufferedImage ImageOne;
				ImageOne = ImageIO.read(fileOne);
				int width = ImageOne.getWidth();// 图片宽度
				int height = ImageOne.getHeight();// 图片高度
				// 从图片中读取RGB
				int[] ImageArrayOne = new int[width * height];
				ImageArrayOne = ImageOne.getRGB(0, 0, width, height,ImageArrayOne, 0, width);

				ImageTwo = QRCodeUtil.createImage("http://www.baidu.com", null, false);
				int widthTwo = ImageTwo.getWidth();// 图片宽度
				int heightTwo = ImageTwo.getHeight();// 图片高度
				int[] ImageArrayTwo = new int[widthTwo * heightTwo];
				ImageArrayTwo = ImageTwo.getRGB(0, 0, widthTwo, heightTwo,
						ImageArrayTwo, 0, widthTwo);

				// 生成新图片
				/*BufferedImage ImageNew = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);// 设置左半部分的RGB
				ImageNew.setRGB(0, 0, width, height, ImageArrayTwo, 0, width);// 设置右半部分的RGB
*/				
				Graphics2D graph = ImageOne.createGraphics();

		        graph.drawImage(ImageTwo, 50, 180, 180, 180, null);
		        graph.drawString("ID:1", 150, 400);
		        graph.dispose();
				
				
				response.setContentType("image/jpg");
				ImageIO.write(ImageOne, "jpg", response.getOutputStream());
				
				//createMark(response, ImageNew, "ID:" + qrcodeId, null, 1,	"方正楷体简体", 30, 1150, 30, Color.WHITE);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void createMark(HttpServletResponse response,
			BufferedImage ImageNew, String markContent, Color markContentColor,
			float qualNum, String fontType, int fontSize, int w, int h,	Color color) {
		markContentColor = color;
		/* 构建要处理的源图片 */
		ImageIcon imageIcon = new ImageIcon(ImageNew);
		/* 获取要处理的图片 */
		Image image = imageIcon.getImage();
		// Image可以获得图片的属性信息
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		// 为画出与源图片的相同大小的图片（可以自己定义）
		BufferedImage bImage = new BufferedImage(width, height,	BufferedImage.TYPE_INT_RGB);
		// 构建2D画笔
		Graphics2D g = bImage.createGraphics();
		/* 设置2D画笔的画出的文字颜色 */
		g.setColor(markContentColor);
		/* 设置2D画笔的画出的文字背景色 */
		g.setBackground(Color.white);
		/* 画出图片 */
		g.drawImage(image, 0, 0, null);

		/* --------对要显示的文字进行处理-------------- */
		AttributedString ats = new AttributedString(markContent);
		Font font = new Font(fontType, Font.BOLD, fontSize);
		g.setFont(font);
		/* 消除java.awt.Font字体的锯齿 */
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		/* 消除java.awt.Font字体的锯齿 */
		// font = g.getFont().deriveFont(30.0f);
		ats.addAttribute(TextAttribute.FONT, font, 0, markContent.length());
		AttributedCharacterIterator iter = ats.getIterator();
		/* 添加水印的文字和设置水印文字出现的内容 ----位置 */
		g.drawString(iter, width - w, height - h);
		/* --------对要显示的文字进行处理--------------*/
		g.dispose();// 画笔结束
		try {
			// 输出 文件 到指定的路径

			/*ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(bImage, "jpg", imOut);
			InputStream in = new ByteArrayInputStream(bs.toByteArray());
			byte[] data = FileUtil.toByteArray(in);
			String filename = "aaa.png";
			response.reset();
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			response.addHeader("Content-Length", "" + data.length);
			response.setContentType("application/octet-stream;charset=UTF-8");
			OutputStream outputStream = new BufferedOutputStream(
					response.getOutputStream());
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
			response.flushBuffer();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
