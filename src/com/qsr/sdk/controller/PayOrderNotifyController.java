package com.qsr.sdk.controller;

import com.qsr.sdk.component.payment.NotifyContent;
import com.qsr.sdk.component.payment.PaymentResponse;
import com.qsr.sdk.controller.render.datarender.Result;
import com.qsr.sdk.service.PayOrderService;
import com.qsr.sdk.service.exception.PaymentServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PayOrderNotifyController extends WebApiController {
    private static final Logger logger = LoggerFactory.getLogger(PayOrderNotifyController.class);

    public PayOrderNotifyController() {
        super(logger);
    }

    public void nofity() {
        NotifyContent notifyContent = null;
        try {
            String firstParam = this.getPara();
            PaymentResponse paymentResult = null;
//            PayOrderService service = this.getService(PayOrderService.class);
            int paymentType = 0;
            try {
                logger.debug("callback,params={}", this.fetch().getParameters());

                paymentType = Integer.parseInt(firstParam);
                Map<String, Object> response = this.fetch().getParameters();

//                paymentResult = service.payOrder(paymentType, response);
//			if (bool) {//支付回调处理成功，继续本地业务逻辑
//				FlowService flowService = this.getComponent(FlowService.class);
//				Map<String, Object> detail = flowService.getOrderDetailByOrderNo(paymentResult.getOrderNumber());
//				String phoneNum = detail.get("description").toString();
//				String productId = detail.get("product_id").toString();
//				int fee = Integer.parseInt(detail.get("payment_fee").toString());
//				phoneNum = phoneNum.substring(phoneNum.indexOf("[")+1, phoneNum.indexOf("]"));
//				flowService.createOutTraderOrder(phoneNum, productId, fee, paymentResult.getOrderNumber());
//				flowService.changeStatus("", paymentResult.getOrderNumber());
//			}
//                notifyContent = service.getNotifyContent(paymentType, paymentResult);

//            } catch (PaymentServiceException e) {
//                logger.error("callback exception,params=" + this.getParameterMap(), e);
//                notifyContent = service.getNotifyContent(paymentType, paymentResult);

            } catch (Exception e) {
                logger.error("callback exception,params=" + this.fetch().getParameters(), e);
                notifyContent = new NotifyContent("failed", null);
            }

            if (paymentResult != null) {

                // NotifyService notifyService=this.newService(new NotifyService());
                // notifyService.addNotify(paymentResult.getOrderNumber());
                try {
//                    NotifyService notifyService = this.getService(NotifyService.class);
//                    notifyService.addNotify(paymentResult.getOrderNumber());
                    // NotifyManager.addNotify(paymentResult.getOrderNumber());
                } catch (Exception e) {
                    logger.error("add notify exception: orderNumber=" + paymentResult.getOrderNumber(), e);
                }

            }
        } catch (Throwable t) {
            notifyContent = new NotifyContent("failed", null);
        }
        this.renderText(notifyContent.getContent(), notifyContent.getContentType());
    }

}
