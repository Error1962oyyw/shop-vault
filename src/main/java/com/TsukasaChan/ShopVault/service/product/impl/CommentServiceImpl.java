package com.TsukasaChan.ShopVault.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import com.TsukasaChan.ShopVault.mapper.product.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【pms_comment(商品评价表)】的数据库操作Service实现
* @createDate 2026-02-13 20:37:02
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




