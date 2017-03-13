package com.hello.zhifu.controller;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hello.zhifu.utils.QRCodeUtil;
import com.hello.zhifu.utils.SettingsUtil;

@Controller
public class ImageController {

	@RequestMapping(value = "qrcode")
	public static void qrcode(String pid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		try {
			String realPath = request.getServletContext().getRealPath("/");
			File fileOne = new File(realPath+"assets\\style\\images\\timg1.jpg");
			BufferedImage ImageOne;
			ImageOne = ImageIO.read(fileOne);
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			
			String rukou = SettingsUtil.getInstance().getString("rukou");
			BufferedImage ImageTwo = QRCodeUtil.createImage(rukou+"/me?pid="+pid, null,	false);
			
			Graphics2D graph = ImageOne.createGraphics();

			graph.drawImage(ImageTwo, (width - 150) / 2, ((height - 150) / 3) * 2, 150, 150, null);
			String markContent = "ID:" + (pid == null ? "" : pid);
			AttributedString ats = new AttributedString(markContent);
			Font font = new Font("方正楷体简体", Font.BOLD, 15);
			graph.setFont(font);
			graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
			ats.addAttribute(TextAttribute.FONT, font, 0, markContent.length());
			AttributedCharacterIterator iter = ats.getIterator();
			graph.drawString(iter, (width - 40) / 2, 408);
			graph.dispose();

			response.setContentType("image/jpg");
			ImageIO.write(ImageOne, "jpg", response.getOutputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
