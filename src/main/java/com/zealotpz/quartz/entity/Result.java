package com.zealotpz.quartz.entity;

import com.zealotpz.quartz.enums.CommonEnum;

/**
 * description: 返回
 *
 * @author: zealotpz
 * create: 2020-09-16 14:19
 **/

public class Result<T> {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    protected String code;
    protected String desc;
    protected T data;


    public Result(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Result(String code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public static <T> Result<T> ok(T t) {
        return new Result<>(CommonEnum.SUCCESS.getCode(), CommonEnum.SUCCESS.getMsg(), t);
    }

    public static <T> Result<T> ok(CommonEnum commonEnum) {
        return new Result<>(commonEnum.getCode(), commonEnum.getMsg());
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> failure(T t) {
        return new Result<>(CommonEnum.FAIL.getCode(), CommonEnum.FAIL.getMsg(), t);
    }

    public static <T> Result<T> failure() {
        return failure(null);
    }

    public boolean isSuccess() {
        return CommonEnum.SUCCESS.getCode().equals(this.code);
    }

}
