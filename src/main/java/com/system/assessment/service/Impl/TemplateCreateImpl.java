package com.system.assessment.service.Impl;

import com.system.assessment.constants.PathConstants;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.service.TemplateCreateService;
import com.system.assessment.utils.FileUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateCreateImpl implements TemplateCreateService {

    @Value("${spring.profiles.active:dev}") // 默认环境为开发环境
    private String activeProfile;

    public File generateWordFile(String name,  Double score) throws IOException {
        XWPFDocument document = new XWPFDocument();
        Resource resource = new ClassPathResource("static/pic/img.png");

        // 标题部分
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setFontFamily("微软雅黑");
        titleRun.setFontSize(18);
        titleRun.setBold(true);
        titleRun.setText("2024H1 个人成长评估报告");

        // 问候部分
        XWPFParagraph greetingParagraph = document.createParagraph();
        greetingParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun greetingRun = greetingParagraph.createRun();
        greetingRun.setFontFamily("微软雅黑");
        greetingRun.setFontSize(10);
        greetingRun.setText("尊敬的 " + name + "：");
        greetingRun.setBold(true);
        greetingRun.addBreak();

        // 正文内容
        addIndentedParagraph(document, "非常感谢您对公司第一次成长评估的积极参与和配合！成长评估是腾微公司“成长=贡献=回报”的践行基石，是诸位同仁以他人为镜、坚持自省，不断成为更加优秀自己的具体执行。");
        addIndentedParagraph(document, "公司的发展，依赖于每一位同仁持续成长、不断提升，当您在腾微大家庭中，不断成为更加优秀自己的时候，腾微也必将伴随您的成长，而不断从追赶，到超越，再到引领。");
        addIndentedParagraph(document, "对于成长评估得分，公司不强调人与人比，而关注自己同自己过去比是否在持续提升、不断前行。前行需要榜样，每一位同仁都能看到公司个人平均得分，以及个人最高得分，公司相信，在榜样的招领下，以及“成长=贡献=回报”的正向激发下，我们必将实现一帮优秀的人不断书写更加优秀的腾微！");
        addIndentedParagraph(document, "您本次评估平均得分为 " + score + "，公司平均得分为 36.5，公司最高得分为 37.8", true, "LEFT", 10);

        // 插入图片部分
        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        File imageFile = resource.getFile();
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            imageRun.addPicture(fis, Document.PICTURE_TYPE_PNG, imageFile.getName(), Units.toEMU(200), Units.toEMU(200));
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        addDocumentBreak(document);
        addDocumentBreak(document);

        // 落款部分
        XWPFParagraph footerParagraph = document.createParagraph();
        footerParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun footerRun = footerParagraph.createRun();
        footerRun.setFontFamily("微软雅黑");
        footerRun.setFontSize(10);
        footerRun.setText("公司 EMT");
        footerRun.addBreak();
        footerRun.setBold(true);
        footerRun.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日")));

        //附件
        XWPFParagraph attachParagraph = document.createParagraph();
        attachParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun attachRun = attachParagraph.createRun();
        attachRun.setFontFamily("微软雅黑");
        attachRun.setFontSize(14);
        attachRun.setText("附件：公司愿景 、价值观及文化理念");
        attachRun.setBold(true);
        attachRun.addBreak();

        addAttachParagraph(document, "【公司愿景】 芯光点亮中国 ，燃遍世界 ，成为中国的 A/I 。",true);
        addAttachParagraph(document, "公司汇聚了光电子领域业界最顶尖人才 ，具备算法 、模拟 、射频 、光学 、芯片开发， 以及先进封装等领域深度研发和垂直整合能力 ，我们也具备 CMOS 、SiGe 、SOI 多种工艺联合设计开发能力 ，我们 将聚焦数据中心光电解决方案场景 ，突围最高端光电芯片 ，成为全球一流的光电核心引擎提供者 。", false);
        addAttachParagraph(document, "芯芯之火可以燎原 ，芯芯之火可以燃遍世界 ，光通信的发展 ，将因我们而改变 。", false);
        addDocumentBreak(document);
        addDocumentBreak(document);
        addAttachParagraph(document, "【公司价值观】 以客户为中心， 以价值为本 ，持续学习， 勇于创造， 坚持自省 ，精品至上 。", true);
        addAttachParagraph(document, "公司存在的价值就是为客户服务，我们通过“创造 ”为客户提供“精品 ”，是我们具体“服务 ”方式 。我们没有任何可以依赖的自然资源， 唯有我们的大脑 、我们的知识 ，我们只有不断学习 ，不断自省， 才能不断提升自己 ，才能用最先进的知识 、技术为客户提供最好的产品 、最好的服务 。", false);
        addAttachParagraph(document, "【公司文化理念】ITrust FIRM WOrD", true);
        addAttachParagraph(document, "业务上 ：创新求实（Innovation ）", true);
        addAttachParagraph(document, "创新 ，就是发现问题（机会） ，并创造性解决问题的过程 。创新是我们的生存法则 ，是将我们的知识和技能转化为社会价值的唯一途径 ，简单复制就是平庸 。", false);
        addAttachParagraph(document, "每个岗位都有创新 ，新的管理体系 、新的评价机制 、新的工具方法等等 ，都是创新 ，创新意味逆流而上 ，意味着不甘平庸 ，意味着组织熵减" ,false);
        addAttachParagraph(document, "求真务实 ，不猜忌 ，简单做事 ，简单做人； 不绕弯子 ，敢讲真话 ，能讲清楚话 ，还要能听的进真话杜绝“捂盖子 ”；", false);
        addAttachParagraph(document, "用最简单 、直接的方式 ，让团队成员充分了解自己 ，理解自己的所说 、所想 、所为；", false);
        addAttachParagraph(document, "求实， 即求真务实 ，诚信是基础 ，拒绝弄虚作假； 求真是勇气也是智慧 ，是刨根问底 、刻苦钻研的精神 ；做事要敢于从根本出发 ，敢于触及问题本质 ，不抹墙皮；深度学习 ，深度思考 ，深度工作 ，刨根 问底 ，追求极致；", false);
        addAttachParagraph(document, "工作中 ：信任互助（ Trust）", true);
        addAttachParagraph(document, "向善而思 ，不度他人之腹", false);
        addAttachParagraph(document, "乐于分享 ，成就他人 ，不贪功 ，不躲过， 勇担当；胜则举杯相庆 ，败则拼死相救", false);
        addAttachParagraph(document, "自己的下游环节 ，就是自己工作价值的呈现 、承载环节，“ 以客户为中心 ”，就是为“客户 ”、为自己的“下游 ”，提供最好的输出 、最好的支撑和最好的服务 。", false);
        addAttachParagraph(document, "团队上： 五湖四海（ Five-Four）", true);
        addAttachParagraph(document, "杜绝土围子 ，拒绝小圈子 ，杜绝公权私用；", false);
        addAttachParagraph(document, "绝不拉帮结派 ，合适的人做合适的事 ，优秀的人做更大的事；", false);
        addAttachParagraph(document, "心胸开阔 ，对事不对人； 能理解 、包容不同的人， 以及不同的声音；", false);


        // 将生成的Word文件保存到本地
        File outputFile = new File("src/main/resources/static/word/"+ name + ".docx");
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            document.write(out);
        }
        return outputFile;
    }

    public static void addIndentedParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setIndentationFirstLine(418); // 设置首行缩进
        paragraph.setAlignment(ParagraphAlignment.BOTH); // 两端对齐
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("微软雅黑");
        run.setFontSize(10);
        run.setText(text);
    }

    public static void addDocumentBreak(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
    }

    public static void addIndentedParagraph(XWPFDocument document, String text, Boolean bold, String alignment, Integer fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setIndentationFirstLine(418); // 设置首行缩进
        if(alignment.equals("LEFT")){
            paragraph.setAlignment(ParagraphAlignment.LEFT);
        }else if(alignment.equals("RIGHT")){
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
        }else if(alignment.equals("CENTER")){
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        }else {
            paragraph.setAlignment(ParagraphAlignment.BOTH);
        }
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("微软雅黑");
        run.setFontSize(fontSize);
        run.setText(text);
        run.setBold(bold);
    }

    public static void addAttachParagraph(XWPFDocument document, String text, Boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("微软雅黑");
        run.setFontSize(10);
        run.setText(text);
        run.setBold(bold);
    }

    @Override
    public String selfReport() {
        return null;
    }

    public File updateWordFile(String name, Map<String, String> variables)throws IOException{
        // 确定保存路径
        String savePath;
        File destinationFile;
        if ("prd".equals(activeProfile)) {
            savePath = PathConstants.TEMPLATE_FOLDER ; // 生产环境路径
            destinationFile = new File(savePath,  "template.docx");
        } else {
            ClassPathResource resource = new ClassPathResource(PathConstants.TEMPLATE_FOLDER);
            File directory = resource.getFile();
            // 保存文件为 template.docx
            destinationFile = new File(directory, "template.docx");
        }
        // 读取现有的 Word 文档
        InputStream fis = new FileInputStream(destinationFile);
        XWPFDocument document = new XWPFDocument(fis);
        String imgPath = "img.png";

        Path folderPath = Paths.get(PathConstants.PICTURE_FOLDER);
        // 判断文件夹是否存在
        if (!Files.exists(folderPath)) {
            try {
                // 创建文件夹
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 遍历段落中的占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
           replacePlaceholdersInParagraph(paragraph, variables, imgPath);
        }

        // 将结果写入到字节数组输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);

        File tempFile = new File(getHistoryDir(), name+".docx");
        File parentDir = tempFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, "创建新文件夹失败!");
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            fileOutputStream.write(outputStream.toByteArray());
        }

        // 返回临时文件
        return tempFile;
    }



    private  void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> variables, String imageName) throws IOException {
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String text = run.getText(0); // 获取当前 Run 的文本
            if (text != null) {
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    String placeholder =  entry.getKey() ;
                    if (text.contains(placeholder)) {
                        // 替换占位符并保留格式
                        text = text.replace(placeholder, entry.getValue());
                        run.setText(text, 0); // 更新文本内容
                    }
                }
            }
            // 检查运行中是否包含图片
            if (!run.getEmbeddedPictures().isEmpty()) {
                // 获取原图片的宽高
                XWPFPicture picture = run.getEmbeddedPictures().get(0);
                long width = picture.getCTPicture().getSpPr().getXfrm().getExt().getCx();
                long height = picture.getCTPicture().getSpPr().getXfrm().getExt().getCy();

                // 移除原图片的运行
                paragraph.removeRun(i);

                // 在相同位置插入新的运行并添加新图片
                XWPFRun newRun = paragraph.insertNewRun(i);
                File imageFile = null;
                if ("prd".equals(activeProfile)) {
                    Path filePath = Paths.get(PathConstants.PICTURE_FOLDER, imageName);
                    imageFile = filePath.toFile();
                } else {
                    ClassPathResource resource = new ClassPathResource(PathConstants.PICTURE_FOLDER);
                    File directory = resource.getFile();
                    // 保存文件为 template.docx
                    imageFile = new File(directory, imageName);
                }

                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    newRun.addPicture(fis, Document.PICTURE_TYPE_PNG, imageName, (int) width, (int) height);
                    paragraph.setSpacingBetween(1.0, LineSpacingRule.AUTO);
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void handlePicture(XWPFParagraph paragraph, String newImagePath) throws IOException {
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            // 检查运行中是否包含图片
            if (!run.getEmbeddedPictures().isEmpty()) {
                // 获取原图片的宽高
                XWPFPicture picture = run.getEmbeddedPictures().get(0);
                long width = picture.getCTPicture().getSpPr().getXfrm().getExt().getCx();
                long height = picture.getCTPicture().getSpPr().getXfrm().getExt().getCy();

                // 移除原图片的运行
                paragraph.removeRun(i);

                // 在相同位置插入新的运行并添加新图片
                XWPFRun newRun = paragraph.insertNewRun(i);
                ClassPathResource resource = new ClassPathResource(newImagePath);
                File imageFile = resource.getFile();
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    newRun.addPicture(fis, Document.PICTURE_TYPE_PNG, newImagePath, (int) width, (int) height);
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public static boolean paragraphContainsImage(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (!run.getEmbeddedPictures().isEmpty()) {
                return true;  // 该段落包含图片
            }
        }
        return false;  // 该段落不包含图片
    }

    private static void clearParagraph(XWPFParagraph paragraph) {
        while (paragraph.getRuns().size() > 0) {
            paragraph.removeRun(0);
        }
    }

    private File getHistoryDir() throws IOException {
        if ("prd".equals(activeProfile)) {
            return new File(PathConstants.HISTORY_FOLDER);
        } else {
            ClassPathResource resource = new ClassPathResource(PathConstants.HISTORY_FOLDER);
            return resource.getFile();
        }
    }
}
