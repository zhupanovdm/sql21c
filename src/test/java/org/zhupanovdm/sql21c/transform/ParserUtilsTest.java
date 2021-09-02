package org.zhupanovdm.sql21c.transform;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserUtilsTest {

    @Test
    public void replace() {
        assertThat(ParserUtils.replaceIncorrectParams(" @123abc=@123abc=123"))
                .isEqualTo(" @_123abc=@_123abc=123");

        assertThat(ParserUtils.replaceIncorrectParams("@a123abc"))
                .isEqualTo("@a123abc");

        assertThat(ParserUtils.replaceIncorrectParams("a123abc"))
                .isEqualTo("a123abc");
    }

}