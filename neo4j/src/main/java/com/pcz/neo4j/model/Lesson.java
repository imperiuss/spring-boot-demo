package com.pcz.neo4j.model;

import com.pcz.neo4j.config.CustomIdStrategy;
import com.pcz.neo4j.constants.NeoConsts;
import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author picongzhi
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Lesson {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * 课程名称
     */
    @NonNull
    private String name;

    /**
     * 任课老师
     */
    @Relationship(NeoConsts.R_TEACHER_OF_LESSON)
    @NonNull
    private Teacher teacher;
}
