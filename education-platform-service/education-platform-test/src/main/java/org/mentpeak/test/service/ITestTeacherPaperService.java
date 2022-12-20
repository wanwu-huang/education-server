package org.mentpeak.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mentpeak.core.mybatisplus.base.BaseService;
import org.mentpeak.test.dto.TeacherPaperDTO;
import org.mentpeak.test.entity.TestTeacherPaper;
import org.mentpeak.test.entity.mongo.TeacherRating;
import org.mentpeak.test.vo.TeacherPaperVO;
import org.mentpeak.test.vo.TestTeacherPaperVO;
import org.springframework.transaction.annotation.Transactional;


/**
 * 教师评定试卷信息表 服务类
 *
 * @author lxp
 * @since 2022-08-15
 */
public interface ITestTeacherPaperService extends BaseService<TestTeacherPaper> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param testTeacherPaper
	 * @return
	 */
	IPage<TestTeacherPaperVO> selectTestTeacherPaperPage(IPage<TestTeacherPaperVO> page, TestTeacherPaperVO testTeacherPaper);



	/**
	 * 题库数据存放到缓存
	 *
	 * @return boolean
	 */
	boolean dataToCache();

	/**
	 * 教师评定
	 * @return 问卷数据
	 */
	TeacherRating generateTeacherRating(Long gradeId, Long classId);

	/**
	 * 保存暂不提交
	 * @param teacherRating
	 * @return
	 */
	boolean saveNotCommit(TeacherRating teacherRating);

	/**
	 * 保存提交
	 * @param teacherRating
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	boolean saveCommit(TeacherRating teacherRating);

	/**
	 * 教师评定列表
	 * @param page
	 * @param teacherPaperDTO
	 * @return
	 */
	Page<TeacherPaperVO> teacherRatingList(IPage<TeacherPaperVO> page, TeacherPaperDTO teacherPaperDTO);
}
