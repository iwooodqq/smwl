package org.example.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.admin.common.convention.result.Result;
import org.example.admin.dto.req.RecycleBinSaveReqDTO;
import org.example.admin.remote.dto.req.ShortLinkCreateDTO;
import org.example.admin.remote.dto.req.ShortLinkPagereqDTO;
import org.example.admin.remote.dto.req.ShortLinkUpdateDTO;
import org.example.admin.remote.dto.res.ShortLinkCountQueryResDTO;
import org.example.admin.remote.dto.res.ShortLinkCreateResDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

public interface LinkRemoteService {
    /**
     * 短链接分页
     */
    static Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gid",shortLinkPagereqDTO.getGid());
        hashMap.put("current",shortLinkPagereqDTO.getCurrent());
        hashMap.put("size",shortLinkPagereqDTO.getSize());
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/admin/v1/page", hashMap);
        return JSON.parseObject(s,new TypeReference<>(){});
    }
    /**
     *短链接新增
     */
    static Result<ShortLinkCreateResDTO> create(ShortLinkCreateDTO shortLinkCreateDTO) {
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/admin/v1/create",JSON.toJSONString(shortLinkCreateDTO));
        return JSON.parseObject(post,new TypeReference<>(){});
    }

    /**
     * 分页查询
     */
    static Result<List<ShortLinkCountQueryResDTO>> listShortLinkCountQueryResDTO(List<String> list){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("requestParam",list);
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", hashMap);
        return JSON.parseObject(s,new TypeReference<>(){});
    }
    /**
     *短链接修改
     */
    static void updateLink(ShortLinkUpdateDTO shortLinkUpdateDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/admin/v1/update",JSON.toJSONString(shortLinkUpdateDTO));
    }
    /**
     * 获取网页标题
     */
    static Result<String>getTitleByUrl(@RequestParam("url")String url){
        String result = HttpUtil.get("/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(result,new TypeReference<>(){});
    }
    /**
     * 移动至回收站
     */
    static void saveRecycleBin( RecycleBinSaveReqDTO recycleBinSaveReqDTO){
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save",JSON.toJSONString(recycleBinSaveReqDTO));
    }
}
