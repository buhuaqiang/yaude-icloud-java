package com.yaude.common.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.util.AntPathMatcher;

/**
 * 使用Spring自身提供的地址匹配工具匹配URL
 */
public class PathMatcherUtil {

    public static void main(String[] args) {
        String url = "/sys/dict/loadDictOrderByValue/tree,s2,2";
        String p = "/sys/dict/loadDictOrderByValue/*";

        System.out.println(PathMatcherUtil.match(p,url));
    }

    /**
     * 實際驗證路徑匹配權限
     *
     * @param matchPath 權限url
     * @param path      訪問路徑
     * @return 是否擁有權限
     */
    public static boolean match(String matchPath, String path) {
        SpringAntMatcher springAntMatcher = new SpringAntMatcher(matchPath, true);
        return springAntMatcher.matches(path);
    }

    /**
     * 實際驗證路徑匹配權限
     *
     * @param list 權限url
     * @param path 訪問路徑
     * @return 是否擁有權限
     */
    public static boolean matches(Collection<String> list, String path) {
        for (String s : list) {
            SpringAntMatcher springAntMatcher = new SpringAntMatcher(s, true);
            if (springAntMatcher.matches(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 地址表達式匹配工具
     */
    private static class SpringAntMatcher implements Matcher {
        private final AntPathMatcher antMatcher;
        private final String pattern;

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }
    }

    private interface Matcher {
        boolean matches(String var1);

        Map<String, String> extractUriTemplateVariables(String var1);
    }
}
