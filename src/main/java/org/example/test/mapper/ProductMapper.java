package org.example.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.example.test.entity.Product;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("update products set commit_stock = commit_stock - #{param2}, version = version + 1 where id = #{param1}")
    int updateStockCancel(Integer productId, Integer productCount);

    @Update("update products set commit_stock = commit_stock - #{param2}, surplus_stock = surplus_stock - #{param2}, version = version + 1 where id = #{param1}")
    int updateStockCommit(Integer productId, Integer productCount);
}
