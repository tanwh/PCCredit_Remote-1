package com.cardpay.pccredit.report.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cardpay.pccredit.report.filter.CustomerMoveFilter;
import com.cardpay.pccredit.report.filter.ReportFilter;
import com.cardpay.pccredit.report.model.BjjdktjbbForm;
import com.cardpay.pccredit.report.service.CustomerTransferFlowService;
import com.wicresoft.jrad.base.auth.IUser;
import com.wicresoft.jrad.base.auth.JRadModule;
import com.wicresoft.jrad.base.auth.JRadOperation;
import com.wicresoft.jrad.base.database.model.QueryResult;
import com.wicresoft.jrad.base.web.JRadModelAndView;
import com.wicresoft.jrad.base.web.controller.BaseController;
import com.wicresoft.jrad.base.web.result.JRadPagedQueryResult;
import com.wicresoft.jrad.base.web.security.LoginManager;
import com.wicresoft.util.spring.Beans;
import com.wicresoft.util.spring.mvc.mv.AbstractModelAndView;

@Controller
@RequestMapping("/refuse/loan/*")
@JRadModule("refuse.loan")
public class RefuseLoanController extends BaseController{
	
	@Autowired
	private CustomerTransferFlowService customerTransferFlowService;
	
	/**
	 * 被拒绝贷款统计查询
	 * @param filter
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "queryRefuseLoan.page", method = { RequestMethod.GET })
	@JRadOperation(JRadOperation.BROWSE)
	public AbstractModelAndView queryHaveBeenLoan(@ModelAttribute ReportFilter filter,HttpServletRequest request) {
		JRadModelAndView mv = new JRadModelAndView("/report/refuseLoan/refuseLoan", request);
		filter.setRequest(request);
		
		IUser user = Beans.get(LoginManager.class).getLoggedInUser(request);
		if(user.getUserType() ==1){
			filter.setUserId(user.getId());
		}
		
	    QueryResult<BjjdktjbbForm> result =  customerTransferFlowService.findbjjdktjbbFormList(filter);
		JRadPagedQueryResult<BjjdktjbbForm> pagedResult = new JRadPagedQueryResult<BjjdktjbbForm>(filter, result);
		mv.addObject(PAGED_RESULT, pagedResult);
		return mv;
	}
	
	
	/**
	 * 导出被拒绝贷款统计查询
	 */
	@ResponseBody
	@RequestMapping(value = "exportAll.page", method = { RequestMethod.GET })
	public void exportAll(@ModelAttribute ReportFilter filter, HttpServletRequest request,HttpServletResponse response){
		filter.setRequest(request);
		IUser user = Beans.get(LoginManager.class).getLoggedInUser(request);
		if(user.getUserType() ==1){
			filter.setUserId(user.getId());
		}
		List<BjjdktjbbForm> list = customerTransferFlowService.getbjjdktjbbFormList(filter);
		create(list,response);
	}
	
	public void create(List<BjjdktjbbForm> list,HttpServletResponse response){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("被拒绝贷款统计统报表");
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//第一行  合并单元格 并且设置标题
		/*HSSFRow row0 = sheet.createRow((int) 0);
		row0.createCell(0).setCellValue("基本资料");
		row0.createCell(0).setCellStyle(style);
		sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 6));*/
		sheet.setColumnWidth(0, 3500);
		sheet.setColumnWidth(1, 8000);
		sheet.setColumnWidth(2, 8000);
		sheet.setColumnWidth(3, 8000);
		sheet.setColumnWidth(4, 8000);
		sheet.setColumnWidth(5, 5000);
		//==========================
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("序号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("客户名称");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("客户证件号码");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("所属产品");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("申请日期");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("申请金额");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("拒绝日期");
		cell.setCellStyle(style);
		cell = row.createCell((short) 7);
		cell.setCellValue("拒绝原因");
		cell.setCellStyle(style);
		cell = row.createCell((short) 8);
		cell.setCellValue("所属客户经理");
		cell.setCellStyle(style);
		cell = row.createCell((short) 9);
		cell.setCellValue("所属机构");
		cell.setCellStyle(style);

		
		for(int i = 0; i < list.size(); i++){
			BjjdktjbbForm move = list.get(i);
			row = sheet.createRow((int) i+1);
			row.createCell((short) 0).setCellValue(move.getRowIndex());
			row.createCell((short) 1).setCellValue(move.getCname());
			row.createCell((short) 2).setCellValue(move.getCardtype());
			row.createCell((short) 3).setCellValue(move.getProdName());
			row.createCell((short) 4).setCellValue(move.getAppdate());
			row.createCell((short) 5).setCellValue(move.getReqlmt());
			row.createCell((short) 6).setCellValue(move.getRefuseDate());
			row.createCell((short) 7).setCellValue(move.getRefuseReason());
			row.createCell((short) 8).setCellValue(move.getBusimanager());
			row.createCell((short) 9).setCellValue(move.getName());
		}
		String fileName = "被拒绝贷款统计报表";
		try{
			response.setHeader("Content-Disposition", "attachment;fileName="+new String(fileName.getBytes("gbk"),"iso8859-1")+".xls");
			response.setHeader("Connection", "close");
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}


