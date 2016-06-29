package com.neusoft.saca.snap.search.controller.search;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.search.api.AppSearchFacade;
import com.neusoft.saca.snap.search.vo.MatchedUser;


@Controller
@RequestMapping("search")
public class SearchRestController {

	@Autowired
	private AppSearchFacade facade;


	/**
	 * 查询用户
	 * 
	 * @param q
	 *            关键字（可为拼音、汉字）
	 * @return
	 */
	@RequestMapping(value = "user/info", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject searchUser(@RequestParam String name, HttpServletRequest request) {
		
		JSONObject result = new JSONObject();
		String encodedName = "";

		try {
			encodedName = new String(name.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result.put("code", "-1");
			result.put("msg", "查询关键字编码错误！");

			return result;
		}

		List<MatchedUser> userVOList = facade.obtainAllMatchedUsers(encodedName);

		// 最多返回10条用户
		if (userVOList.size() > 10) {
			userVOList = userVOList.subList(0, 10);
		}
		
		for(MatchedUser matchedUser:userVOList){
			if(matchedUser.getHighlightSnippetsMap() == null){
				matchedUser.setHighlightSnippetsMap(new HashMap<String, List<String>>());
			}
		}
		result.put("code", "0");
		result.put("result", userVOList);
		
		return result;
	}
}
