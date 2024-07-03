package org.example.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.dto.req.GroupSaverReqDTO;
import org.example.admin.dto.req.GroupSortReqDTO;
import org.example.admin.dto.req.GroupUpdateReqDTO;
import org.example.admin.dto.res.GroupResDto;
import org.example.admin.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> saveGroup(@RequestBody GroupSaverReqDTO groupSaverReqDTO) {
        groupService.savegroup(groupSaverReqDTO.getName());
        return Results.success();
    }
    /**
     * 查询分组排序
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupResDto>> sortgroup() {
        List<GroupResDto> resDtoList=groupService.sort();
        return Results.success(resDtoList);
    }
    /**
     * 修改分组名称
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO groupUpdateReqDTO) {
        groupService.updategroup(groupUpdateReqDTO);
        return Results.success();
    }
    /**
     * 删除分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deletegroup(@RequestParam String gid){
        groupService.deletegroup(gid);
       return Results.success();
    }
    /**
     * 设置分组排序
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void>sortgroup(@RequestBody List<GroupSortReqDTO> groupSortReqDTOList) {
        groupService.sortgroup(groupSortReqDTOList);
        return Results.success();
    }
}
