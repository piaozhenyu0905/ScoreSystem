package com.system.assessment.template;

public class panelTemplate {

    public static String htmlTemplateTest(String name){
        return String.format(
                "<html>" +
                        "<body style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #333; text-align: center;\" >2024H1 个人成长评估报告</h2>" +
                        "<p><strong>尊敬的 %s:</strong></p>" +
                        "<p>&nbsp;&nbsp;您好！</p>" +
                        "<p>&nbsp;&nbsp;非常感谢您对公司第一次成长评估的积极参与和配合！成长评估是腾讯公司 “成长 = 贡献 = 回报” 的践行基石，是诸位同仁以他人为镜、坚持自省，不断成为更加优秀自己的具体执行。</p>" +
                        "<p>&nbsp;&nbsp;公司的发展，依赖于每一位同仁持续成长，不断提升，当您在腾讯大家庭中，不断成为更加优秀自己的时候，腾讯也必将伴随您的成长，而不断从追赶，到超越，再到引领。</p>" +
                        "<p>&nbsp;&nbsp;对于成长评估得分，公司不强调人与人对比，而关注自己同自己过去比是否在持续提升、不断前行。</p>" +
                        "<p>&nbsp;&nbsp;前行需要榜样，每一位同仁都能看到公司个人平均得分，以及个人最高得分，公司相信，在榜样的招领下，以及“成长=贡献=回报”的正向激发下，我们必将实现——帮优秀的人不断书写更加优秀的腾讯！</p>" +
                        "<p>&nbsp;&nbsp;<strong>您本次评估平均得分为36，公司类型的平均得分为37，公司类型的平均得分为38</strong></p>" +
                        "</body>" +
                        "</html>",
                name);
    }

    public static String htmlTemplate2(String name, String template){
        return String.format(
                "<html>" +
                        "<body style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #333; text-align: center;\">2024H1 个人成长评估报告</h2>" +
                        "<p><strong>尊敬的 %s:</strong></p>" +
                        "<p style=\"text-indent: 2em;\">您好！</p>" +
                        "<p style=\"text-indent: 2em;\">%s </p>" +
                        "<p style=\"text-indent: 2em;\"><strong>您本次评估平均得分为36，公司类型的平均得分为37，公司类型的平均得分为38</strong></p>" +
                        "</body>" +
                        "</html>",
                name, template);
    }

    public static String htmlTemplate(String name, String template, Double totalScore, String lxyz, Double lxyzTotalScore, String business, Double businessTotalScore){
        return String.format(
                "<html>" +
                        "<body style=\"font-family: Arial, sans-serif;\">" +
                        "<h2 style=\"color: #333; text-align: center;\">2024H1 个人成长评估报告</h2>" +
                        "<p><strong>尊敬的 %s:</strong></p>" +
                        "<p style=\"text-indent: 2em;\" >您好!</p>" +
                        "<p style=\"text-indent: 2em;\" >%s</p>" +
                        "<p style=\"text-indent: 2em;\" ><strong>您本次评估平均得分为 %.1f，公司%s类型的平均得分为 %.1f，公司%s类型的平均得分为 %.1f</strong>，详细的得分情况，请前往成长评估系统查看!</p>" +
                        "</body>" +
                        "</html>",
                name, template, totalScore, lxyz, lxyzTotalScore, business, businessTotalScore);
    }


}
