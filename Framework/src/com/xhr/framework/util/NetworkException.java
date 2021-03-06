package com.xhr.framework.util;

import com.xhr.framework.R;

/**
 * 网络异常
 *
 * @author wang.xy
 */
public class NetworkException extends Exception {

    public static final int UNKNOWN_HOST_EXCEPTION_CODE = -1;
    private static final long serialVersionUID = 4521612743569217432L;
    private static String MESSAGE = PackageUtil.getString(R.string.network_is_not_available);
    private int exceptionCode;

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int mExceptionCode) {
        this.exceptionCode = mExceptionCode;
    }

    /**
     * 构造函数
     */
    public NetworkException() {
        super(MESSAGE);
    }

    /**
     * 构造函数
     *
     * @param message 网络异常的内容
     */
    public NetworkException(String message) {
        super(message);
    }
}
