package com.example.demo.controller;

import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.OrderManagerVM;
import com.example.demo.service.wms.OrderManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordermanager")
public class OrderManagerController {


    @Autowired
//    @Resource
    private OrderManagerService orderManagerService;

    @RequestMapping("/addOrder")
    public MessageResult<Void> addOrder(@RequestBody OrderManagerVM orderManagerVM) {
        return orderManagerService.addOrder(orderManagerVM);
    }

    //http://localhost:8081/ordermanager/addOrderAndOrderDetails
       /*
    {
	"orderNumber":"12345678",
	"productOrderDetail":[
		{
			"productid":2115,
			"count":30,
			"dealprice":25.5
		},
			{
			"productid":2114,
			"count":208,
			"dealprice":20
		}
		]

}
     */
    @RequestMapping("/addOrderAndOrderDetails")
    public MessageResult<Void> addOrderAndOrderDetails(@RequestBody OrderManagerVM orderManagerVM) {
        try {
            return orderManagerService.addOrderAndOrderDetails(orderManagerVM);
        }
        catch (Exception ex)
        {
            MessageResult<Void> messageResult = new MessageResult<>();
            messageResult.setMessage(ex.getMessage());
            return messageResult;
        }

    }

    @PostMapping("/deleteOrder")
    public MessageResult<Void> deleteOrder(@RequestBody Order order) {
        return orderManagerService.deleteOrder(order);
    }


}
