package com.zhupanovdm.sql21c.transform;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserUtilsTest {

    @Test
    public void replaceIncorrectParams() {
        assertThat(ParserUtils.replaceIncorrectParams(" @123abc=@123abc=123"))
                .isEqualTo(" @_123abc=@_123abc=123");

        assertThat(ParserUtils.replaceIncorrectParams("@a123abc"))
                .isEqualTo("@a123abc");

        assertThat(ParserUtils.replaceIncorrectParams("a123abc"))
                .isEqualTo("a123abc");
    }

    @Test
    public void toEntityName() {
        assertThat(ParserUtils.toEntityName("[abc.def]"))
                .isEqualTo("abc.def");

        assertThat(ParserUtils.toEntityName("[abc]"))
                .isEqualTo("abc");
    }

    @Test
    public void toDboName() {
        assertThat(ParserUtils.toDboName("abc"))
                .isEqualTo("abc");

        assertThat(ParserUtils.toDboName("abc.def"))
                .isEqualTo("[abc.def]");
    }

}