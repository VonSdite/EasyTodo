package xyz.wendyltanpcy.easytodo.model;

import org.litepal.crud.DataSupport;

/**
 * 存储网页信息类
 */

public class WebPage extends DataSupport {
    private String url;
    private String pageName;

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

