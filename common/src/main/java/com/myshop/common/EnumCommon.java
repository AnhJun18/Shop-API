package com.myshop.common;

public class EnumCommon {
    public static enum EStatus{
        WAITPAYMENT ("Chờ thanh toán"),
        WAITCONFIRM   ("Chờ xác nhận"),
        PAPERING   ("Đang chuẩn bị hàng"),
        DELIVERING    ("Đang vận chuyển"),
        DONE ("Đã hoàn thành"),
        CANCEL  ("Đã hủy");
        private final String name;
        EStatus( String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
