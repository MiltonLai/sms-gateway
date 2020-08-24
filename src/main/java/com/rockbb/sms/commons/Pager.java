package com.rockbb.sms.commons;

import java.io.Serializable;

public class Pager implements Serializable {
    public static final String[] ORDERS = {"DESC", "ASC"};
    public static final int DESC = 0;
    public static final int ASC = 1;
    public static final int MAX = Integer.MAX_VALUE - 1;

    private int offset;
    private int limit;
    private int intSort;
    private int intSort2;
    private String sort;
    private String order;
    private String sort2;
    private String order2;

    public Pager() {
        this(0, 20, 0, 0);
    }

    public Pager(int limit) {
        this(0, limit, 0, 0);
    }

    public Pager(int offset, int limit) { this(offset, limit, 0, 0); }

    public Pager(int offset, int limit, int sort, int order) {
        this.offset = (offset < 0)? 1 : offset + 1;
        this.limit = (limit <= 0)? 21 : limit + offset + 1;
        this.intSort = sort;
        this.order = (order == DESC)? ORDERS[0] : ORDERS[1];
    }

    public Pager(int offset, int limit, int sort, int order, int sort2, int order2) {
        this.offset = (offset < 0)? 1 : offset + 1;
        this.limit = (limit <= 0)? 21 : limit + offset + 1;
        this.intSort = sort;
        this.order = (order == DESC)? ORDERS[0] : ORDERS[1];
        this.intSort2 = sort2;
        this.order2 = (order2 == DESC)? ORDERS[0] : ORDERS[1];
    }

    public Pager max() {
        this.limit = MAX;
        return this;
    }

    /**
     * 设置第二层的排序规则
     */
    public Pager setSort2(int sort2, int order2) {
        this.intSort2 = sort2;
        this.order2 = (order2 == DESC)? ORDERS[0] : ORDERS[1];
        return this;
    }

    public Pager setSorts(String[] sorts) {
        if (intSort > sorts.length - 1 || intSort < 0) {
            this.sort = sorts[0];
        } else {
            this.sort = sorts[intSort];
        }
        if (intSort2 > sorts.length - 1 || intSort2 < 0) {
            this.sort2 = sorts[0];
        } else {
            this.sort2 = sorts[intSort2];
        }
        return this;
    }

    public int getOffset() { return offset; }
    public int getLimit() { return limit; }
    public int getIntSort() { return intSort; }
    public String getSort() { return sort; }
    public String getSort2() { return sort2; }
    public String getOrder() { return order; }
    public String getOrder2() { return order2; }
}