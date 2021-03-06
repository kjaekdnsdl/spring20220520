package com.choong.spr.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choong.spr.domain.BoardDto;
import com.choong.spr.domain.ReplyDto;
import com.choong.spr.service.BoardService;
import com.choong.spr.service.ReplyService;

@Controller
@RequestMapping("board")
public class BoardController {
	
	@Autowired
	private BoardService service;
	
	@Autowired
	private ReplyService replyService;

	@RequestMapping("list")
	public void list(@RequestParam(name = "keyword", defaultValue = "") String keyword,
					 @RequestParam(name = "type", defaultValue = "") String type,
			         Model model) {
		List<BoardDto> list = service.listBoard(type, keyword);
		model.addAttribute("boardList", list);
	}
	
	@GetMapping("insert")
	public void insert() {
		
	}
	
	@PostMapping("insert")
	public String insert(BoardDto board,
						 MultipartFile[] file,
						 Principal principal, 
				   	 	 RedirectAttributes rttr) {
		
		/*	System.out.println(file);
			System.out.println(file.getOriginalFilename());
			System.out.println(file.getSize());
			*/
//		if (file.getSize() > 0) {
//			board.setFileName(file.getOriginalFilename());
//			
//		}
		
		if(file != null) {
			List<String> fileList = new ArrayList<String>();
			for (MultipartFile f : file) {
				fileList.add(f.getOriginalFilename());
			}
			
			board.setFileName(fileList);
		}
		
		board.setMemberId(principal.getName());
		boolean success = service.insertBoard(board, file);
		
		if (success) {
			rttr.addFlashAttribute("message", "??? ?????? ?????????????????????.");
		} else {
			rttr.addFlashAttribute("message", "??? ?????? ???????????? ???????????????.");
		}
		
		return "redirect:/board/list";
	}
	
	@GetMapping("get")
	public void get(int id, Model model) {
		BoardDto dto = service.getBoardById(id);
		List<ReplyDto> replyList = replyService.getReplyByBoardId(id);
		model.addAttribute("board", dto);
		
		/* ajax??? ???????????? ?????? ?????? */
		// model.addAttribute("replyList", replyList);
		
	}
	
	@PostMapping("modify")
	public String modify(BoardDto dto, Principal principal, RedirectAttributes rttr) {
		BoardDto oldBoard = service.getBoardById(dto.getId());
		
		if (oldBoard.getMemberId().equals(principal.getName())) {
			boolean success = service.updateBoard(dto);
			
			if (success) {
				rttr.addFlashAttribute("message", "?????? ?????????????????????.");
			} else {
				rttr.addFlashAttribute("message", "?????? ???????????? ???????????????.");
			}
			
		} else {
			rttr.addFlashAttribute("message", "????????? ????????????.");
		}
		
		rttr.addAttribute("id", dto.getId());
		return "redirect:/board/get";
		
	}
	
	@PostMapping("remove")
	public String remove(BoardDto dto, Principal principal, RedirectAttributes rttr) {
		
		// ????????? ?????? ??????
		BoardDto oldBoard = service.getBoardById(dto.getId());
		// ????????? ?????????(memberId)??? principal??? name??? ???????????? ?????? ?????? ??????.
		if (oldBoard.getMemberId().equals(principal.getName())) {
			boolean success = service.deleteBoard(dto.getId());
			
			if (success) {
				rttr.addFlashAttribute("message", "?????? ?????? ???????????????.");
				
			} else {
				rttr.addFlashAttribute("message", "?????? ?????? ?????????????????????.");
			}
			
		} else {
			rttr.addFlashAttribute("message", "????????? ????????????.");
			rttr.addAttribute("id", dto.getId());
			return "redirect:/board/get";
		}
		
		return "redirect:/board/list";
	}
}










