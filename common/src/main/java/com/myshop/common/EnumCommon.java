package com.myshop.common;

public class EnumCommon {
    public static enum EStatus{
        WAITPAYMENT ("Chờ thanh toán"),
        WAITCONFIRM   ("Chờ xác nhận"),
        PAPERING   ("Đang chuẩn bị hàng"),
        DELIVERING    ("Đang vận chuyển"),
        DONE ("Đã hoàn thành"),
        CANCEL  ("Đã hủy"),
        DELAY1  ("Hoãn giao hàng lần 1"),
        DELAY2 ("Hoãn giao hàng lần 2"),
        SHIPCANCEL ("Shipper hủy giao hàng");
        private final String name;
        EStatus( String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
