package com.ruoyi.svc;

import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/svc")
public class SvcController {

    @RequestMapping(value = "pf")
    public R<Integer> pf(@RequestParam("a") String a, @RequestParam("b") String b){
        return R.ok(calculateScore(a,b));
    }

    // 计算编辑距离
    public static int levenshteinDistance(String s1, String s2) {
        // sdasdasdasdadsad    sdasdasdasdadsa
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    // 计算相似度
    public static double similarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1 - (double) distance / maxLength;
    }

    // 根据相似度计算得分，本题满分为 14 分
    public static int calculateScore(String answer, String studentAnswer) {
        double similarity = similarity(answer, studentAnswer);
        return (int) (similarity * 14);
    }

}
