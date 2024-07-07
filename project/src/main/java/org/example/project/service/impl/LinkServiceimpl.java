package org.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.project.common.convention.exception.ClientException;
import org.example.project.common.convention.exception.ServiceException;
import org.example.project.common.enums.VailDateTypeEnum;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dao.entity.LinkGotoDO;
import org.example.project.dao.mapper.LinkMapper;
import org.example.project.dao.mapper.ShortLinkGotoMapper;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
import org.example.project.dto.req.ShortLinkUpdateDTO;
import org.example.project.dto.res.ShortLinkCountQueryResDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.LinkService;
import org.example.project.toolkit.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;
import static org.example.project.common.constant.RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY;

@AllArgsConstructor
@Service
@Slf4j
public class LinkServiceimpl extends ServiceImpl<LinkMapper,LinkDO> implements LinkService{
    private final RBloomFilter<String> rBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    /**
     * 创建短链接
     */
    @Override
    public ShortLinkCreateResDTO create(ShortLinkCreateDTO shortLinkCreateDTO) {
        String generateSuffix = generateSuffix(shortLinkCreateDTO);
        String fullShortUrl=shortLinkCreateDTO.getDomain()+"/"+generateSuffix;
        LinkDO linkDO = BeanUtil.toBean(shortLinkCreateDTO, LinkDO.class);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(generateSuffix);
        linkDO.setEnableStatus(0);
        LinkGotoDO linkGotoDO = LinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(shortLinkCreateDTO.getGid())
                .build();
        try {
            baseMapper.insert(linkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        }
        catch (DuplicateKeyException e) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
            LinkDO linkDO1 = baseMapper.selectOne(queryWrapper);
            if (linkDO1 != null) {
                log.warn("短链接{}重复入库",fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }
        }
        rBloomFilter.add(fullShortUrl);
        return  ShortLinkCreateResDTO.builder()
                .fullShortUri("http://"+linkDO.getFullShortUrl())
                .originUri(shortLinkCreateDTO.getOriginUrl())
                .gid(shortLinkCreateDTO.getGid())
                .build();
    }
    /**
     * 短链接分页
     */
    @Override
    public IPage<ShortLinkPageresDTO>pagelink(ShortLinkPagereqDTO shortLinkPagereqDTO) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, shortLinkPagereqDTO.getGid())
                .eq(LinkDO::getDelFlag, 0)
                .eq(LinkDO::getEnableStatus, 0);
        ShortLinkPagereqDTO shortLinkPagereqDTO1 = baseMapper.selectPage(shortLinkPagereqDTO, queryWrapper);
        return shortLinkPagereqDTO1.convert(each->{
            ShortLinkPageresDTO bean = BeanUtil.toBean(each, ShortLinkPageresDTO.class);
            bean.setDomain("http://"+bean.getDomain());
            return bean;
        });

    }
    /**
     * 短链接分组内数量
     */
    @Override
    public List<ShortLinkCountQueryResDTO> listShortLinkCountQueryResDTO(List<String> requestParam) {
        QueryWrapper<LinkDO> queryWrapper = Wrappers.query(new LinkDO())
                .select("gid as gid,count(*) as  LinkCount")
                .in("gid", requestParam)
                .eq("enable_Status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps,ShortLinkCountQueryResDTO.class);
    }
    /**
     *短链接修改
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLink(ShortLinkUpdateDTO shortLinkUpdateDTO) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, shortLinkUpdateDTO.getGid())
                .eq(LinkDO::getDelFlag, 0)
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getFullShortUrl, shortLinkUpdateDTO.getFullShortUrl());
        LinkDO haslinkDO = baseMapper.selectOne(queryWrapper);
        if (haslinkDO==null){
            throw new ClientException("短链接不存在");
        }
        LinkDO linkDO = LinkDO.builder()
                .domain(haslinkDO.getDomain())
                .shortUri(haslinkDO.getShortUri())
                .clickNum(haslinkDO.getClickNum())
                .favicon(haslinkDO.getFavicon())
                .createdType(haslinkDO.getCreatedType())
                .gid(shortLinkUpdateDTO.getGid())
                .originUrl(shortLinkUpdateDTO.getOriginUrl())
                .describe(shortLinkUpdateDTO.getDescribe())
                .validDate(shortLinkUpdateDTO.getValidDate())
                .validDateType(shortLinkUpdateDTO.getValidDateType())
                .build();
        if (Objects.equals(haslinkDO.getGid(), shortLinkUpdateDTO.getGid())) {
            LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, haslinkDO.getGid())
                    .eq(LinkDO::getFullShortUrl, shortLinkUpdateDTO.getFullShortUrl())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0)
                    .set(Objects.equals(shortLinkUpdateDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), LinkDO::getValidDate, null);
            baseMapper.update(linkDO,wrapper);
        }
        else {
            LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, shortLinkUpdateDTO.getGid())
                    .eq(LinkDO::getFullShortUrl, shortLinkUpdateDTO.getFullShortUrl())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0)
                    .set(Objects.equals(shortLinkUpdateDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), LinkDO::getValidDate, null);
            baseMapper.delete(wrapper);
            baseMapper.insert(linkDO);
        }
    }
    /**
     * 跳转短链接
     */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response)  {
        String serverName = request.getServerName();
        String fullShortUri = serverName + "/" + shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUri));
        if (StrUtil.isNotBlank(originalLink)){
            ((HttpServletResponse)response).sendRedirect(originalLink);
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUri));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUri));
            if (originalLink!=null){
                ((HttpServletResponse)response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<LinkGotoDO> linkGotoDOLambdaQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                    .eq(LinkGotoDO::getFullShortUrl, fullShortUri);
            LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(linkGotoDOLambdaQueryWrapper);
            if (linkGotoDO == null) {
                return;
            }
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, linkGotoDO.getGid())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0)
                    .eq(LinkDO::getFullShortUrl, fullShortUri);
            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
            if (linkDO != null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUri),linkDO.getOriginUrl());
                ((HttpServletResponse)response).sendRedirect(linkDO.getOriginUrl());
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 生成短链接方法
     */
    private String generateSuffix(ShortLinkCreateDTO shortLinkCreateDTO){
        int GenerateCount=0;
        String originUri;
        while (true){
            if(GenerateCount>10){
                throw new ServiceException("短链接频繁生成请稍后再试");
            }
            String originUrl = shortLinkCreateDTO.getOriginUrl();
            originUrl+=System.currentTimeMillis();
            originUri = HashUtil.hashToBase62(originUrl);
            if (!rBloomFilter.contains("http://"+"/"+originUri)){
                break;
            }
            GenerateCount++;
        }
        return originUri;
    }
}
