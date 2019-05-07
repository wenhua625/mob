package com.novarise.webase.util;

import java.util.HashMap;
import java.util.Map;

import com.pingan.scf.core.server.entity.api.ApiRequestVo;
import com.pingan.scf.core.server.service.ApiSecurityUserService;
import com.pingan.scf.core.server.service.security.ApiSecurityService;
import com.pingan.scf.core.server.service.security.impl.ApiSecurityServiceImpl2;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String , Object> map = new  HashMap<String , Object>();
		//map.put("2", "vlues");
		
		ApiSecurityUserService  userService = new UserServiceImpl();
		ApiSecurityService apiSecurityService = new ApiSecurityServiceImpl2();
		apiSecurityService.setUserService(userService);
		
		//解封请求数据
		ApiRequestVo apiRequestVo = apiSecurityService.decapsulateClientRequest(map);
		
		map= apiRequestVo.getData();
		System.out.println("----"+map.get(""));
	}

}
