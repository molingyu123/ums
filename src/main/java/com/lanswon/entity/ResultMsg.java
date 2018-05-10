package com.lanswon.entity;

import java.io.Serializable;

public class ResultMsg implements Serializable {
    private static final long serialVersionUID = -5230789528043956195L;
    // 定义常用的code
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private int code;
    private String msg;
    private Object rows;
    private Object data;
    private int page;
    private int pageSize;
    private int records;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ResultMsg() {
    }

    public ResultMsg(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResultMsg(int code, String msg, Object rows) {
        super();
        this.code = code;
        this.msg = msg;
        this.rows = rows;
        this.data = rows;
    }

    public ResultMsg(int code, String msg, Object rows, int count) {
        super();
        this.code = code;
        this.msg = msg;
        this.rows = rows;
        this.data = rows;
        this.records = count;
    }

    public void setPage(int pageNum, int pageSize, int count) {
        this.page = pageNum;
        this.pageSize = pageSize;
        this.records = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object data) {
        this.rows = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int pageNum) {
        this.page = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultMsg [code=" + code + ", msg=" + msg + ", rows=" + rows + ", data=" + data + ", page=" + page
                + ", pageSize=" + pageSize + ", records=" + records + ", total=" + total + "]";
    }

}
