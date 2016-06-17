package com.example.administrator.testit.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleUtils {

    public static Connection getConnection() {
        String url = "jdbc:oracle:thin:@10.1.15.73:1521/testmag";
        String username = "PLATFORM_TEST";
        String password = "PLATFORM_TEST";
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void update(String sql) {
        PreparedStatement st;
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(true);
            st = conn.prepareStatement(sql);
            st.executeUpdate();
            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeSQL(String sql) {
        ResultSet resultSet = null;
        Statement st;
        Connection conn = getConnection();
        try {
            st = (Statement) conn.createStatement();
            resultSet = st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static String getStringResult(String sql) {
        String result = null;
        ResultSet resultSet = executeSQL(sql);
        try {
            while (resultSet.next()) {
                result = resultSet.getString("result");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteNonDefaultBankCard(String username) {
        //修改非默认银行卡为已删除状态
        String memberId = getMemberId(username);
        update("update tbl_bank_card set is_delete='Y' where member_id='" + memberId + "' and withdraw_enabled='N'");
    }

    public static String getMemberId(String username) {
        //获取member_id
        return getStringResult("select member_id as result from tbl_member_login_info where user_name='" + username + "'");
    }


    public static void updateMobile(String mobile) {
        //修改用户手机号码为默认手机号码13800138000
        update("update tbl_member set mobile='13800138000' where mobile='" + mobile + "'");
    }

    public static  void setMobile(String username,String mobile){
        String member_id = OracleUtils.getMemberId(username);
        update ("update tbl_member set mobile = '"+mobile+"' where id = '"+member_id+"'");
    }

    public static String getDefaultBankCardNumber(String username) {
        //获取默认银行卡
        return getStringResult("select card_name as result from tbl_bank_card \n" +
                "where member_id=(select member_id from tbl_member_login_info where user_name='" + username + "')\n" +
                "and withdraw_enabled='Y'");
    }

    public static void updateIDCard(String idCard) {
        //修改id_card 为默认值100000000000000000
        update("update tbl_id_card set id_card_no='100000000000000000' where id_card_no='" + idCard + "'");
        update("update tbl_member set id_card='100000000000000000' where id_card='" + idCard + "'");
    }

    public static void updateAmountBalance(String username) {
        //修改用户主账户的可用余额，和总额
        String member_id = getMemberId(username);
        update("UPDATE TBL_MEMBER_ACCOUNT SET AMOUNT_BALANCE=100000 WHERE MEMBER_ID='" + member_id + "' and type='PRIMARY'");
    }

    public static void updateInvestSXBRecord(String username,String productName) {
        //修改用户投资随鑫宝的投资时间为前一天
        String investment_id=getStringResult("select id from \n" +
                "(select * from tbl_member_investment where \n" +
                "member_id=(select member_id from tbl_member_login_info where user_name='"+username+"') \n" +
                "and product_item_id=(select id from tbl_product_item where name='"+productName+"') order by create_time desc)where rownum=1");
        update("update tbl_member_investment set create_time=create_time-1 where id='"+investment_id+"'");
    }

    public static void updateTradePassword(String username) {
        //修改交易密码为默认beijing
        String member_id = getMemberId(username);
        update("update tbl_member set trade_password='75b11bbc38aad6236605e70a165afff317d9b648' where id='" + member_id + "'");
    }

    public static void updateLoginPassword(String username) {
        //修改登录密码为默认的q1w2e3
        String member_id = getMemberId(username);
        update("update tbl_member_login_info set password='7cbb3252ba6b7e9c422fac5334d22054' where member_id='" + member_id + "'");
    }

    public static void updateRechargeForIppjr_Login_Test() {
        update("UPDATE TBL_MEMBER_ACCOUNT acc SET acc.AMOUNT_BALANCE = '100000' WHERE EXISTS" +
                " (SELECT 1 FROM TBL_MEMBER_LOGIN_INFO info WHERE INFO.MEMBER_ID = ACC.MEMBER_ID  AND INFO.USER_NAME =" +
                " 'ippjr_login_test' AND acc.type ='PRIMARY')");
    }

    public static void updateUnfreezeAccount(String username) {
        //解冻账号
        String memberId = getMemberId(username);
        update("update tbl_member_login_info set login_failed_count = 0 where user_name ='" + username + "'");
        update("update tbl_member set status='VALID' where id='" + memberId + "'");
    }

    public static void deleteRedEnvelopes(String username) {
        //更新用户红包状态为已使用
        String memberId = getMemberId(username);
        update("update tbl_member_coupon set status=1 where type=1 and  member_uuid='" + memberId + "'");
    }

    public static void updateAuthStatus(String username) {
        //更新用户手机号码，身份证开户，交易密码，完成开户流程
        String memberId = getMemberId(username);
        update("update tbl_member set real_name='tester',sex='FEMALE',age='18',mobile='13800138000',mobile_status='VALID',id_card='100000000000000000',id_card_status='VALID',status='VALID',trade_password='75b11bbc38aad6236605e70a165afff317d9b648'\n" +
                "where id='" + memberId + "'");
    }

    //客户账户
    public static BigDecimal getMember_account() {
        //客户账户
        return NumberUtil.format2BigDecimal(getStringResult("select SUM(FROZEN_AMOUNT+amount_balance) as result from tbl_member_account where type='PRIMARY'"));
    }

    public static BigDecimal getAmount_balance() {
        //客户余额
        return NumberUtil.format2BigDecimal(getStringResult("select SUM(amount_balance) as result from tbl_member_account where type='PRIMARY'"));
    }

    public static BigDecimal getFrozen_amount() {
        //冻结金额
        return NumberUtil.format2BigDecimal(getStringResult("select SUM(FROZEN_AMOUNT) as result from tbl_member_account where type='PRIMARY'"));
    }

    public static BigDecimal getInvesting_frozen_amount() {
        //未满标冻结金额
        String investing_frozen_amount=getStringResult("select sum(fund_amount) as result from tbl_member_fund_record where fund_type='FROZENINVESTMENT' \n" +
                "and remark not like '%随鑫宝-投资%'\n" +
                "and member_investment_id in (select id from tbl_member_investment where status='INVESTING')");
        return NumberUtil.format2BigDecimal(investing_frozen_amount);
    }

    public static BigDecimal getMember_withdraw_frozen_amount() {
        //客户提现冻结金额
        String frozen_amount=getStringResult("select sum(payment_amount) as result  from tbl_payment_task where task_status in ('CREATED','PROCESSING') and payment_user_type='MEMBER'");
        return NumberUtil.format2BigDecimal(frozen_amount);
    }

    public static BigDecimal getRedeem_frozen_amount() {
        //随鑫宝赎回冻结金额
        String frozen_amount=getStringResult("select SUM(FUND_AMOUNT) as result from tbl_member_fund_record where fund_type='REDEEMSFAILURE' \n" +
                "AND ID IN (select FUND_RECORD_ID from tbl_fund_delay WHERE STATUS='FREEZE')");
        return NumberUtil.format2BigDecimal(frozen_amount);
    }

    public static BigDecimal getDeposit_amount() {
        //沉淀资金账户
        return NumberUtil.format2BigDecimal(getStringResult("select SUM(amount_balance) as result from tbl_member_account where type='PRIMARY' and recharge_amount=0"));
    }

    public static BigDecimal getFlow_amount() {
        //流动资金账户
        return NumberUtil.format2BigDecimal(getStringResult("select SUM(amount_balance) as result from tbl_member_account where type='PRIMARY' and recharge_amount!=0"));
    }

    //平台账户
    public static BigDecimal getPlatform_balance_amount() {
        //平台余额
        String platform_balance_amount=OracleUtils.getStringResult("select PLATFORM_BALANCE as result from tbl_platform_account");
        return NumberUtil.format2BigDecimal(platform_balance_amount);
    }

    public static BigDecimal getXpress_reserve_balance() {
        //准备金余额
        return NumberUtil.format2BigDecimal(getStringResult("select XPRESS_RESERVE_BALANCE as result from tbl_platform_account"));
    }

    public static BigDecimal getXpress_working_balance() {
        //随鑫宝余额
        return NumberUtil.format2BigDecimal(getStringResult("select XPRESS_WORKING_BALANCE as result from tbl_platform_account"));
    }

    public static BigDecimal getActivity_balance() {
        //获取活动账户
        return NumberUtil.format2BigDecimal(getStringResult("select ACTIVITY_BALANCE as result from tbl_platform_account"));
    }

    public static BigDecimal getCommission_balance() {
        //获取手续费账户
        return NumberUtil.format2BigDecimal(getStringResult("select COMMISSION_BALANCE as result from tbl_platform_account"));
    }

    public static BigDecimal getAllocate_balance() {
        //获取待拨付账户
        String  allocate_balance=getStringResult("select ALLOCATE_BALANCE as result from tbl_platform_account");
        return NumberUtil.format2BigDecimal(allocate_balance);
    }

    public static BigDecimal getPlatform_frozen_amount() {
        //平台提现冻结
        return NumberUtil.format2BigDecimal(getStringResult("select PLATFORM_FROZEN_AMOUNT as result from tbl_platform_account"));
    }

    public static BigDecimal getAllocate_frozen_amount() {
        //已拨付冻结账户
        return NumberUtil.format2BigDecimal(getStringResult("select ALLOCATE_FROZEN_AMOUNT as result from tbl_platform_account"));
    }

    public static BigDecimal getReserve_withdraw_frozen_amount() {
        //准备金提现冻结
        return NumberUtil.format2BigDecimal(getStringResult("select XPRESS_RESERVE_FROZEN_AMOUNT as result from tbl_platform_account"));
    }

    public static BigDecimal getXpress_withdraw_frozen_amount() {
        //随鑫宝提现冻结金额
        return NumberUtil.format2BigDecimal(getStringResult("select XPRESS_WORKING_FROZEN_AMOUNT as result from tbl_platform_account"));
    }

    public static BigDecimal getAllinpay_balance() {
        //通联余额
        return NumberUtil.format2BigDecimal(getStringResult("select  balance as result from tbl_payment_channel_account where payment_channel=0"));
    }
    public static BigDecimal getUmpay_balance() {
        //联动余额
        return NumberUtil.format2BigDecimal(getStringResult("select  balance as result from tbl_payment_channel_account where payment_channel=1"));
    }

    public static BigDecimal getRechargeOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("SELECT sum(payment_amount) as result FROM tbl_payment_record \n" +
                "WHERE payment_status='SUCCEED' \n" +
                "and payment_type='RECHARGE'\n" +
                "AND CREATE_TIME >=sysdate-1"));
    }

    public static BigDecimal getAllinpayRechargeOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("SELECT SUM(PAYMENT_AMOUNT) AS RESULT FROM tbl_payment_record \n" +
                "WHERE payment_channel='ALLINPAY' \n" +
                "and payment_status='SUCCEED' \n" +
                "AND CREATE_TIME >=sysdate-1"));
    }

    public static BigDecimal getUmpayRechargeOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("SELECT SUM(PAYMENT_AMOUNT) AS RESULT FROM tbl_payment_record \n" +
                "WHERE payment_channel='UMPAY' \n" +
                "and payment_status='SUCCEED' \n" +
                "AND CREATE_TIME >=sysdate-1"));
    }

    public static BigDecimal getAllinpayTodaySettlement(){
        return NumberUtil.format2BigDecimal(getStringResult("SELECT SUM(PAYMENT_AMOUNT) AS RESULT FROM tbl_payment_record \n" +
                "WHERE payment_channel='ALLINPAY' \n" +
                "and payment_status='SUCCEED' \n" +
                "AND CREATE_TIME >= TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP-1, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')\n" +
                "AND CREATE_TIME <TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')"));
    }

    public static BigDecimal getUmpayTodaySettlement(){
        return NumberUtil.format2BigDecimal(getStringResult("SELECT SUM(PAYMENT_AMOUNT) AS RESULT FROM tbl_payment_record \n" +
                "WHERE payment_channel='UMPAY' \n" +
                "and payment_status='SUCCEED' \n" +
                "AND CREATE_TIME >= TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP-1, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')\n" +
                "AND CREATE_TIME <TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')"));
    }

    public static BigDecimal getWithdrawOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("select sum(payment_amount) as result from tbl_payment_task \n" +
                "where task_status in ('CREATED','PROCESSING')\n" +
                "and create_time>=sysdate-1"));
    }

    public static BigDecimal getAllinpayWithdrawOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("select sum(payment_amount) as result from tbl_payment_task \n" +
                "where  payment_channel='ALLINPAY' \n" +
                "and task_status in ('CREATED','PROCESSING')\n" +
                "and create_time>=sysdate-1"));
    }

    public static BigDecimal getUmpayWithdrawOnWay(){
        return NumberUtil.format2BigDecimal(getStringResult("select sum(payment_amount) as result from tbl_payment_task \n" +
                "where  payment_channel='UMPAY' \n" +
                "and task_status in ('CREATED','PROCESSING')\n" +
                "and create_time>=sysdate-1"));
    }


    public static BigDecimal getAllinpayTodayWithdraw(){
        return NumberUtil.format2BigDecimal(getStringResult("select sum(payment_amount) as result from tbl_payment_task \n" +
                "where payment_channel='ALLINPAY' \n" +
                "and task_status in ('CREATED','PROCESSING')\n" +
                "AND CREATE_TIME >= TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP-1, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')\n" +
                "AND CREATE_TIME <TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF'"));
    }

    public static BigDecimal getUmpayTodayWithdraw(){
        return NumberUtil.format2BigDecimal(getStringResult("select sum(payment_amount) as result from tbl_payment_task \n" +
                "where payment_channel='UMPAY' \n" +
                "and task_status in ('CREATED','PROCESSING')\n" +
                "AND CREATE_TIME >= TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP-1, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')\n" +
                "AND CREATE_TIME <TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP, 'YYYY-MM-DD'),'YYYY-MM-DD HH24:MI:SS.FF')"));
    }



}
