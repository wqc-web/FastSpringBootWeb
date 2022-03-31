package com.zhongzhou.api.controller;


    import org.springframework.web.bind.annotation.*;
    import com.zhongzhou.common.base.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.Constants;
import com.zhongzhou.common.base.Pager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.zhongzhou.api.service.impl.PositionServiceImpl;
import com.zhongzhou.api.entity.Position;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
/**
 * <p>
 * 职位 前端控制器
 * </p>
 *
 * @author wqc
 * @since 2021-06-05
 */
@RestController
@RequestMapping("/api/position")
@Slf4j
public class PositionController extends BaseController {
    @Resource
    private PositionServiceImpl positionService;

    /**
     * 分页查询列表
     *
     * @param pager 分页信息
     * @param position Position
     * @return ReturnEntity
     */
    @GetMapping("/page")
    public ReturnEntity selectPageList(Pager<Position> pager, Position position,
        HttpServletRequest request,HttpServletResponse response){
        try{
            QueryWrapper<Position> wrapper=new QueryWrapper<>();
            List<Position> records= positionService.page(pager,wrapper).getRecords();
            int count= positionService.count(wrapper);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS,count,records);
        }catch(Exception e){
            e.printStackTrace();
            log.error("["+Constants.MSG_FIND_FAILED+"]:"+e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED,null,null);
        }
    }

    /**
     * 查询所有列表
     * @param position Position
     * @return ReturnEntity
     */
    @GetMapping("/list")
    public ReturnEntity selectList(Position position,
        HttpServletRequest request,HttpServletResponse response){
        try{
            QueryWrapper<Position> wrapper=new QueryWrapper<>();
            List<Position> list= positionService.list(wrapper);
            return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS,list.size(),list);
        }catch(Exception e){
            e.printStackTrace();
            log.error("["+Constants.MSG_FIND_FAILED+"]:"+e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED,null,null);
        }
    }

    /**
     * 查询详情
     *
     * @param id 主键
     * @return ReturnEntity
     */
    @GetMapping("/detail/{id}")
    public ReturnEntity selectById(@PathVariable("id") Long id){
        try{
            Position position = positionService.getById(id);
            if(null!= position){
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, position);
            }else{
                return new ReturnEntitySuccess(Constants.MSG_FIND_NOT_FOUND);
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error("[id:{} " + Constants.MSG_FIND_FAILED + "]:{}", id, e.getMessage());
            return new ReturnEntityError(Constants.MSG_FIND_FAILED);
        }
    }

    /**
     * 新增
     *
     * @param position Position
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PostMapping("/add")
    public ReturnEntity save(@Validated @RequestBody Position position,BindingResult result,
            HttpServletRequest request,HttpServletResponse response){
        if(result.hasErrors()){
            FieldError fieldError=result.getFieldErrors().get(0);
            String errorMsg=fieldError.getDefaultMessage();
            if(Constants.MSG_ERROR_CANNOT_NULL.equals(errorMsg)){
                errorMsg=fieldError.getField()+fieldError.getDefaultMessage();
            }
            return new ReturnEntityError(errorMsg,null, position);
        }else{
            try {
                QueryWrapper<Position> wrapper=new QueryWrapper<>();

                    if(positionService.save(position)){
                        return new ReturnEntitySuccess(Constants.MSG_INSERT_SUCCESS, position);
                    }else{
                        return new ReturnEntityError(Constants.MSG_INSERT_FAILED, position);
                    }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("[" + Constants.MSG_INSERT_FAILED + "]:{}", e.getMessage());
                return new ReturnEntityError(Constants.MSG_INSERT_FAILED, position);
            }
        }
     }

    /**
     * 修改
     *
     * @param id 主键
     * @param position Position
     * @param result BindingResult
     * @return ReturnEntity
     */
    @PutMapping("/edit/{id}")
    public ReturnEntity updateById(@PathVariable("id") Long id,@Validated @RequestBody Position position,BindingResult result,
            HttpServletRequest request,HttpServletResponse response){
        if(result.hasErrors()){
            return new ReturnEntityError(result.getFieldErrors().get(0).getDefaultMessage(), position);
        }else{
            try {
                if(null== positionService.getById(id)){
                    return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND, position);
                }else{
                    if(positionService.updateById(position)){
                        return new ReturnEntitySuccess(Constants.MSG_UPDATE_SUCCESS, position);
                    }else{
                        return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, position);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("[id:{} " + Constants.MSG_UPDATE_FAILED + "]:{}", id, e.getMessage());
                return new ReturnEntityError(Constants.MSG_UPDATE_FAILED, position);
            }
        }
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ReturnEntity
     */
    @DeleteMapping("/delete/{id}")
    public ReturnEntity deleteById(@PathVariable("id") Long id){
        try {
            if(null== positionService.getById(id)){
                return new ReturnEntityError(Constants.MSG_FIND_NOT_FOUND,id);
            }else{
                if(positionService.removeById(id)){
                    return new ReturnEntitySuccess(Constants.MSG_DELETE_SUCCESS,id);
                }else{
                    return new ReturnEntityError(Constants.MSG_DELETE_FAILED,id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[id:{} " + Constants.MSG_DELETE_FAILED + "]:{}", id, e.getMessage());
            return new ReturnEntityError(Constants.MSG_DELETE_FAILED, id);
        }
    }

}
