package org.example.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.admin.common.convention.result.Result;
import org.example.admin.dto.req.RecycleBinSaveReqDTO;
import org.example.admin.dto.req.ShortLinkStatsReqDTO;
import org.example.admin.dto.res.ShortLinkStatsRespDTO;
import org.example.admin.remote.dto.req.*;
import org.example.admin.remote.dto.res.ShortLinkCountQueryResDTO;
import org.example.admin.remote.dto.res.ShortLinkCreateResDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;

import org.example.admin.remote.dto.res.ShortLinkStatsAccessRecordRespDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LinkRemoteService {
    /**
     * 短链接分页
     */
    static Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gid",shortLinkPagereqDTO.getGid());
        hashMap.put("orderTag", shortLinkPagereqDTO.getOrderTag());
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
    /**
     * 回收站短链接分页
     */
    static Result<IPage<ShortLinkPageresDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPagereqDTO shortLinkRecycleBinPagereqDTO){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gidList",shortLinkRecycleBinPagereqDTO.getGidList());
        hashMap.put("current",shortLinkRecycleBinPagereqDTO.getCurrent());
        hashMap.put("size",shortLinkRecycleBinPagereqDTO.getSize());
        String s = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", hashMap);
        return JSON.parseObject(s,new TypeReference<>(){});
    }
    /**
     * 短链接恢复
     */
    static void recoverRecycleBin(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover",JSON.toJSONString(recycleBinRecoverReqDTO));
    }
    /**
     * 短链接删除
     */
    static void removeRecycleBin(RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove",JSON.toJSONString(recycleBinRemoveReqDTO));
    }

    /**
     * 短链接监控详情
     */
    static Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 短链接监控访问记录数据
     */
    static Result<IPage<ShortLinkStatsAccessRecordRespDTO>>shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/access-record", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    static Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
