package com.ugoodtech.umi.manager.filter;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.renderer.DefaultWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kotone
 * Date: 2016/12/12
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCaptchaFilter extends HttpServlet {

    private static final String PARAM_HEIGHT = "height"; //高度 默认为50

    private static final String PARAM_WIDTH = "width";//宽度 默认为200

    private static final String PAEAM_NOISE = "noise";//干扰线条 默认是没有干扰线条

    private static final String PAEAM_TEXT = "text";//文本

    protected int _width = 200;
    protected int _height = 50;
    protected boolean _noise = false;
    protected String _text = null;

    /**
     * 初始化过滤器.将配置文件的参数文件赋值
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        if (getInitParameter(PARAM_HEIGHT) != null) {
            _height = Integer.valueOf(getInitParameter(PARAM_HEIGHT));
        }

        if (getInitParameter(PARAM_WIDTH) != null) {
            _width = Integer.valueOf(getInitParameter(PARAM_WIDTH));
        }

        if (getInitParameter(PAEAM_NOISE) != null) {
            _noise = Boolean.valueOf(getInitParameter(PAEAM_NOISE));
        }

        if (getInitParameter(PAEAM_NOISE) != null) {
            _text = String.valueOf(getInitParameter(PAEAM_TEXT));
        }
    }

    /**
     * 因为获取图片只会有get方法
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Font> fontList = new ArrayList<Font>();
        fontList.add(new Font("黑体", Font.BOLD, 20));//可以设置斜体之类的
        //fontList.add(new Font("Courier", Font.BOLD, 40));
           /*List<Color> colorList = new ArrayList<Color>();
           colorList.add(Color.green);
           colorList.add(Color.BLUE);
           DefaultWordRenderer dwr=new DefaultWordRenderer(colorList,fontList);*/

        //加入多种颜色后会随机显示 字体空心
        List<Color> colorList = new ArrayList<Color>();
        colorList.add(Color.black);
        // colorList.add(Color.white);
//        colorList.add(Color.blue);
        DefaultWordRenderer cwr = new DefaultWordRenderer(colorList, fontList);

        WordRenderer wr = cwr;
//        ColoredEdgesWordRenderer wordRenderer = new ColoredEdgesWordRenderer(COLORS, FONTS);
        Captcha captcha = new Captcha.Builder(60, 25)
                .addText(wr)
//                .addNoise()
                .build();
        req.getSession().setAttribute("simpleCaptcha", captcha);
        CaptchaServletUtil.writeImage(resp, captcha.getImage());

    }
}