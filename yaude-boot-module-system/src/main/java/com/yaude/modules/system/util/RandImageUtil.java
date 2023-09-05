package com.yaude.modules.system.util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 登錄驗證碼工具類
 */
public class RandImageUtil {

    public static final String key = "JEECG_LOGIN_KEY";

    /**
     * 定義圖形大小
     */
    private static final int width = 105;
    /**
     * 定義圖形大小
     */
    private static final int height = 35;

    /**
     * 定義干擾線數量
     */
    private static final int count = 200;

    /**
     * 干擾線的長度=1.414*lineWidth
     */
    private static final int lineWidth = 2;

    /**
     * 圖片格式
     */
    private static final String IMG_FORMAT = "JPEG";

    /**
     * base64 圖片前綴
     */
    private static final String BASE64_PRE = "data:image/jpg;base64,";

    /**
     * 直接通過response 返回圖片
     * @param response
     * @param resultCode
     * @throws IOException
     */
    public static void generate(HttpServletResponse response, String resultCode) throws IOException {
        BufferedImage image = getImageBuffer(resultCode);
        // 輸出圖象到頁面
        ImageIO.write(image, IMG_FORMAT, response.getOutputStream());
    }

    /**
     * 生成base64字符串
     * @param resultCode
     * @return
     * @throws IOException
     */
    public static String generate(String resultCode) throws IOException {
        BufferedImage image = getImageBuffer(resultCode);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        //寫入流中
        ImageIO.write(image, IMG_FORMAT, byteStream);
        //轉換成字節
        byte[] bytes = byteStream.toByteArray();
        //轉換成base64串
        String base64 = Base64.getEncoder().encodeToString(bytes).trim();
        base64 = base64.replaceAll("\n", "").replaceAll("\r", "");//刪除 \r\n

        //寫到指定位置
        //ImageIO.write(bufferedImage, "png", new File(""));

        return BASE64_PRE+base64;
    }

    private static BufferedImage getImageBuffer(String resultCode){
        // 在內存中創建圖象
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 獲取圖形上下文
        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        // 設定背景顏色
        graphics.setColor(Color.WHITE); // ---1
        graphics.fillRect(0, 0, width, height);
        // 設定邊框顏色
//		graphics.setColor(getRandColor(100, 200)); // ---2
        graphics.drawRect(0, 0, width - 1, height - 1);

        final Random random = new Random();
        // 隨機產生干擾線，使圖象中的認證碼不易被其它程序探測到
        for (int i = 0; i < count; i++) {
            graphics.setColor(getRandColor(150, 200)); // ---3

            final int x = random.nextInt(width - lineWidth - 1) + 1; // 保證畫在邊框之內
            final int y = random.nextInt(height - lineWidth - 1) + 1;
            final int xl = random.nextInt(lineWidth);
            final int yl = random.nextInt(lineWidth);
            graphics.drawLine(x, y, x + xl, y + yl);
        }
        // 取隨機產生的認證碼
        for (int i = 0; i < resultCode.length(); i++) {
            // 將認證碼顯示到圖象中,調用函數出來的顏色相同，可能是因為種子太接近，所以只能直接生成
            // graphics.setColor(new Color(20 + random.nextInt(130), 20 + random
            // .nextInt(130), 20 + random.nextInt(130)));
            // 設置字體顏色
            graphics.setColor(Color.BLACK);
            // 設置字體樣式
//			graphics.setFont(new Font("Arial Black", Font.ITALIC, 18));
            graphics.setFont(new Font("Times New Roman", Font.BOLD, 24));
            // 設置字符，字符間距，上邊距
            graphics.drawString(String.valueOf(resultCode.charAt(i)), (23 * i) + 8, 26);
        }
        // 圖象生效
        graphics.dispose();
        return image;
    }

    private static Color getRandColor(int fc, int bc) { // 取得給定范圍隨機顏色
        final Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }

        final int r = fc + random.nextInt(bc - fc);
        final int g = fc + random.nextInt(bc - fc);
        final int b = fc + random.nextInt(bc - fc);

        return new Color(r, g, b);
    }
}
