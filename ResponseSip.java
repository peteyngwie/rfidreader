package com.smartcity.api;

public class ResponseSip {


        private int status;
        private String message;
        private Sip result;

        private boolean ok;
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public Sip getResult() {
            return result;
        }

        public void setResult(Sip result) {
            this.result = result;
        }
        public boolean isOk() {
            return ok;
        }
        public void setOk(boolean ok) {
            this.ok = ok;
        }

    }



