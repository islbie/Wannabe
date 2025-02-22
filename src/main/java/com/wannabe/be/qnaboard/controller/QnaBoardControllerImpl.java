package com.wannabe.be.qnaboard.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.attoparser.config.ParseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.wannabe.be.member.vo.MemberVO;
import com.wannabe.be.qnaboard.service.QnaBoardService;
import com.wannabe.be.qnaboard.vo.QnaReplyVO;
import com.wannabe.be.qnaboard.vo.QnaVO;
import com.wannabe.be.utills.Criteria;
import com.wannabe.be.utills.PaginationVO;

@Controller("qnaBoardController")
public class QnaBoardControllerImpl implements QnaBoardController {

	@Autowired
	private QnaBoardService qnaBoardService;

	@Override
	@RequestMapping(value = "/product/postreply", method = { RequestMethod.POST, RequestMethod.GET })
	public String postReply(@RequestParam(value = "product_no") int product_no,
			@RequestParam(value = "parent_no") int parent_no,
			@RequestParam(value = "qna_reply_content") String qna_reply_content) throws DataAccessException {

		QnaReplyVO qnaReplyVO = new QnaReplyVO();
		qnaReplyVO.setParent_no(parent_no);
		qnaReplyVO.setProduct_no(product_no);
		qnaReplyVO.setQna_reply_content(qna_reply_content);

		qnaBoardService.postReply(qnaReplyVO);

		return "qnalist";

	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/product/modifyqna", method = { RequestMethod.POST, RequestMethod.GET })
	public int modifyQna(@RequestParam(value = "qna_no") int qna_no,
			@RequestParam(value = "qna_content") String qna_content) throws DataAccessException {

		QnaVO qnaVO = new QnaVO();
		qnaVO.setQna_no(qna_no);
		qnaVO.setQna_content(qna_content);

		return qnaBoardService.modifyQna(qnaVO);

	}

	@Override
	@RequestMapping(value = "/product/qna", method = { RequestMethod.GET })
	public ModelAndView loginCheck(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		if ((session.getAttribute("isLogOn") == null)) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>alert('癒쇱� 濡쒓렇�씤�쓣 �빐二쇱꽭�슂.'); location.href='/member/loginform';</script>");
				out.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ModelAndView("redirect:/product/qnaform");
	}

	@Override
	@RequestMapping(value = "/product/qnaform", method = { RequestMethod.GET })
	public String viewQnaBoard() {
		return "qnaform";
	}

	@Override
	@RequestMapping(value = "/product/uploadQna", method = { RequestMethod.POST })
	public ModelAndView uploadQna(@RequestParam(value = "title") String title,
			@RequestParam(value = "qna_content") String qna_content,
			@RequestParam(value = "member_id") String member_id, @RequestParam(value = "product_no") int product_no)
			throws DataAccessException {
		QnaVO qnaVO = new QnaVO();
		qnaVO.setMember_id(member_id);
		qnaVO.setTitle(title);
		qnaVO.setQna_content(qna_content);
		qnaVO.setProduct_no(product_no);

		/*
		 * String displayName = member_id.substring(0, 3);
		 * System.out.println(displayName); for (int i = 0; i < member_id.length() - 3;
		 * i++) { displayName += "*"; }
		 */

		qnaBoardService.writeQna(qnaVO);

		/*
		 * List<QnaVO> qnaList = qnaBoardService.listQna(product_no); List<QnaReplyVO>
		 * replyList = qnaBoardService.listQnaReply(product_no); ModelAndView mv = new
		 * ModelAndView("qnaBoard"); mv.addObject("qnaList", qnaList);
		 * mv.addObject("replyList", replyList);
		 * 
		 * mv.addObject("displayName", displayName);
		 */
		return new ModelAndView("redirect:/product/qnaBoard?product_no=" + product_no);
	}
	
	
	@RequestMapping(value="product/updateQnaStatus", method = {RequestMethod.POST})
	public void updateQnaStatus(@RequestParam("qna_no") int qna_no) throws DataAccessException{
		 qnaBoardService.updateQnaStatus(qna_no);
	}
	@Override
	@ResponseBody
	@RequestMapping(value = "/product/viewCount", method = { RequestMethod.GET })
	public int updateQnaViews(@RequestParam("qna_no") int qna_no) throws DataAccessException {
		return qnaBoardService.updateQnaViews(qna_no);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/product/getViewCount", method = { RequestMethod.GET })
	public QnaVO getQnaViews(@RequestParam("qna_no") int qna_no) throws DataAccessException {
		QnaVO qnaVO = new QnaVO();
		qnaVO = qnaBoardService.getQnaViews(qna_no);
		return qnaVO;
	}

	@ResponseBody // member_id �븘�씠�뵒 �꽆寃⑥＜寃� �닔�젙 �븘�슂
	@RequestMapping(value = "product/getreplies/{parent_no}", method = { RequestMethod.GET })
	public List<QnaReplyVO> getReply(@PathVariable("parent_no") int parent_no) throws DataAccessException {

		List<QnaReplyVO> qnaReplyList = qnaBoardService.fetchReplies(parent_no);

		return qnaReplyList;

	}

	@Override
	@ResponseBody // member_id �븘�씠�뵒 �꽆寃⑥＜寃� �닔�젙 �븘�슂
	@RequestMapping(value = "product/deleteqna", method = { RequestMethod.POST, RequestMethod.GET })
	public int deleteQna(@RequestParam(value = "qna_no") int qna_no) throws DataAccessException {
		return qnaBoardService.deleteQna(qna_no);
	}

	@Override
	@GetMapping(value = "/product/qnaBoard")
	public ModelAndView listQnawithPaging(Criteria cri, @RequestParam("product_no") int product_no, HttpServletRequest request, HttpServletResponse response) {
	

		PaginationVO paginationVO = new PaginationVO();
		paginationVO.setCri(cri);
		paginationVO.setTotalCount(qnaBoardService.countAllQna(product_no));
		List<QnaVO> qnaList = qnaBoardService.listQnaWithPaging(product_no, cri);
		List<QnaReplyVO> replyList = qnaBoardService.listQnaReply(product_no);
		String temp = "";
		for(int i = 0; i < qnaList.size(); i++) {
			 temp = qnaList.get(i).getMember_id();
			 String displayName = temp.substring(0,3);
			for(int j = 0; j < temp.length()-3; j++) {		
				displayName += "*";		
				qnaList.get(i).setDisplayName(displayName);		   
			}
				
		}		
		ModelAndView mv = new ModelAndView("board/qnaBoard");
		mv.addObject("product_no", product_no);
		mv.addObject("pageInfo", paginationVO);
		mv.addObject("qnaList", qnaList);
		mv.addObject("replyList", replyList);
		return mv;
	}


}
