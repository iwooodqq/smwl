package org.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.project.common.convention.exception.ClientException;
import org.example.project.common.convention.exception.ServiceException;
import org.example.project.common.enums.VailDateTypeEnum;
import org.example.project.dao.entity.*;
import org.example.project.dao.mapper.*;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
import org.example.project.dto.req.ShortLinkUpdateDTO;
import org.example.project.dto.res.ShortLinkCountQueryResDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.LinkService;
import org.example.project.toolkit.HashUtil;
import org.example.project.toolkit.LinkUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.example.project.common.constant.RedisKeyConstant.*;
import static org.example.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;
import static org.example.project.toolkit.LinkUtil.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class LinkServiceimpl extends ServiceImpl<LinkMapper,LinkDO> implements LinkService{
    private final RBloomFilter<String> rBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsDoMapper linkOsStatsDoMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String amapkey;
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
        linkDO.setFavicon(getFavicon(shortLinkCreateDTO.getOriginUrl()));
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
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                shortLinkCreateDTO.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(shortLinkCreateDTO.getValidDate()),
                TimeUnit.MILLISECONDS
        );
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
            shortLinkStats(fullShortUri,null,request,response);
            ((HttpServletResponse)response).sendRedirect(originalLink);
            return;
        }
        boolean contains = rBloomFilter.contains(fullShortUri);
        if (!contains){
            ((HttpServletResponse)response).sendRedirect("/page/notfound");
            return;
        }
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUri));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)){
            ((HttpServletResponse)response).sendRedirect("/page/notfound");
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUri));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUri));
            if (originalLink!=null){
                shortLinkStats(fullShortUri,null,request,response);
                ((HttpServletResponse)response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<LinkGotoDO> linkGotoDOLambdaQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                    .eq(LinkGotoDO::getFullShortUrl, fullShortUri);
            LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(linkGotoDOLambdaQueryWrapper);
            if (linkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUri),"-",30, TimeUnit.MINUTES);
                ((HttpServletResponse)response).sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, linkGotoDO.getGid())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 0)
                    .eq(LinkDO::getFullShortUrl, fullShortUri);
            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
            if (linkDO == null||linkDO.getValidDate().before(new Date())) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUri),"-",30, TimeUnit.MINUTES);
                ((HttpServletResponse)response).sendRedirect("/page/notfound");
                return;
            }
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUri),
                    linkDO.getOriginUrl(),
                    getLinkCacheValidTime(linkDO.getValidDate()),
                    TimeUnit.MILLISECONDS
            );
            shortLinkStats(fullShortUri,linkDO.getGid(),request,response);
            ((HttpServletResponse)response).sendRedirect(linkDO.getOriginUrl());
        }finally {
            lock.unlock();
        }
    }

    /**
     * 短链接监控方法
     */
    private void shortLinkStats(String fullShortUrl,String gid,ServletRequest request, ServletResponse response){
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        Runnable addResponseCookieTask=()->{
            String uv = UUID.fastUUID().toString();
            Cookie uvCookie = new Cookie("uv", uv);
            uvCookie.setMaxAge(60*60*24*30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl,fullShortUrl.indexOf("/"),fullShortUrl.length()));
            ((HttpServletResponse)response).addCookie(uvCookie);
            atomicBoolean.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv);
        };
        if (ArrayUtil.isNotEmpty(cookies)){
            Arrays.stream(cookies)
                    .filter(each->Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each->{
                        Long add = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, each);
                        atomicBoolean.set(add!=null&&add> 0L);
                    },addResponseCookieTask);
        }
        else {
            addResponseCookieTask.run();
        }
        String remoteAddr = getIp((HttpServletRequest) request);
        Long uipAdd =stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, remoteAddr);
        boolean uipFirstFlag=uipAdd!=null&&uipAdd>0L;
        if (StrUtil.isBlank(gid)){
            LambdaQueryWrapper<LinkGotoDO> wrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                    .eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
            LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(wrapper);
            gid = linkGotoDO.getGid();
        }
        int hour = DateUtil.hour(new Date(), true);
        Week week = DateUtil.dayOfWeekEnum(new Date());
        int weekValue = week.getIso8601Value();
        LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                .pv(1)
                .uv(atomicBoolean.get()?1:0)
                .uip(uipFirstFlag?1:0)
                .hour(hour)
                .weekday(weekValue)
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(new Date())
                .build();
        linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
        HashMap<String, Object> map = new HashMap<>();
        map.put("key",amapkey);
        map.put("ip", remoteAddr);
        String localResultStr = HttpUtil.get(AMAP_REMOTE_URL, map);
        JSONObject jsonObject = JSON.parseObject(localResultStr);
        String infocode = jsonObject.getString("infocode");
        LinkLocaleStatsDo linkLocaleStatsDo;
        if (StrUtil.isNotBlank(infocode)&&StrUtil.equals(infocode,"10000")){
            String province = jsonObject.getString("province");
            boolean unknown = StrUtil.equals(province,"[]");
            linkLocaleStatsDo = LinkLocaleStatsDo.builder()
                    .fullShortUrl(fullShortUrl)
                    .province(unknown ?"未知":province)
                    .city(unknown ?"未知":jsonObject.getString("city"))
                    .adcode(unknown ?"未知":jsonObject.getString("adcode"))
                    .cnt(1)
                    .country("中国")
                    .gid(gid)
                    .date(new Date())
                    .build();
            linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDo);
            LinkOsStatsDo linkOsStatsDo = LinkOsStatsDo.builder()
                    .gid(gid)
                    .os(getOs((HttpServletRequest) request))
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(new Date())
                    .build();
            linkOsStatsDoMapper.shortLinkOsState(linkOsStatsDo);
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
    /**
     * 获取网站图标
     */
    @SneakyThrows
    private String getFavicon(String url) {
        //创建URL对象
        URL targetUrl = new URL(url);
        //打开连接
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        // 禁止自动处理重定向
        connection.setInstanceFollowRedirects(false);
        // 设置请求方法为GET
        connection.setRequestMethod("GET");
        //连接
        connection.connect();
        //获取响应码
        int responseCode = connection.getResponseCode();
        // 如果是重定向响应码
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            //获取重定向的URL
            String redirectUrl = connection.getHeaderField("Location");
            //如果重定向URL不为空
            if (redirectUrl != null) {
                // 创建新的URL对象
                URL newUrl = new URL(redirectUrl);//打开新的连接
                connection = (HttpURLConnection) newUrl.openConnection();//设置请求方法为GET
                connection.setRequestMethod("GET");//连接
                connection.connect();//获取新的响应码
                responseCode = connection.getResponseCode();
            }
        }
        // 如果响应码为200(HTTP_OK)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut)?icon]").first();
            if (faviconLink!=null){
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }
}
