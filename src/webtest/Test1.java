/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author hallm8
 */
public class Test1 {

    public void runTest1() {

        List<String> ouList = new ArrayList<>();

        int count = 0;
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader("C:\\Users\\hallm8\\Documents\\NetBeansProjects\\WebTest\\src\\webtest\\OU"));
            String str;

            while ((str = in.readLine()) != null) {
                ouList.add(str);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }

        FirefoxProfile fp = new FirefoxProfile(new File("C:\\Users\\hallm8\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\tiu8eb0h.default"));
        fp.setPreference("webdriver.load.strategy", "unstable");
        WebDriver driver = new FirefoxDriver(fp);

        driver.manage().window().maximize();
        driver.get("http://byui.brightspace.com/d2l/login?noredirect=true");
        WebElement myDynamicElement = (new WebDriverWait(driver, 60))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("d2l_minibar_placeholder")));

        // times out after 60 seconds
        Actions actions = new Actions(driver);
        for (String ouList1 : ouList) {
            WebDriverWait wait = new WebDriverWait(driver, 60);
            /**
             * PULLING VALENCE REQUESTS
             *
             * Step 1: Open up Selenium and authenticate by having the user sign
             * in. This bypasses the Authorization Protection
             *
             * Step 2: Open up HTTP Client and pass the cookies into it.
             *
             * Step 3: Open up the JSON parser of your choosing and parse into
             * it!
             */

            try {

                Set<Cookie> seleniumCookies = driver.manage().getCookies();
                CookieStore cookieStore = new BasicCookieStore();

                for (Cookie seleniumCookie : seleniumCookies) {
                    BasicClientCookie basicClientCookie
                            = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
                    basicClientCookie.setDomain(seleniumCookie.getDomain());
                    basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
                    basicClientCookie.setPath(seleniumCookie.getPath());
                    cookieStore.addCookie(basicClientCookie);
                }
                HttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
                HttpGet request = new HttpGet("https://byui.brightspace.com/d2l/api/le/1.7/" + ouList1 + "/content/toc");
                request.addHeader("accept", "application/json");
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                String jsonString = EntityUtils.toString(response.getEntity());

                JSONObject obj = new JSONObject(jsonString);
                JSONArray modules = obj.getJSONArray("Modules");
                System.out.println(jsonString);

                for (int i = 0; i < modules.length(); i++) {
                    if (modules.getJSONObject(i).has("Modules")) {
                        modules.put(modules.getJSONObject(i).getJSONArray("Modules"));
                    }

                    System.out.println(modules.get(i));
                    JSONArray topics = modules.getJSONObject(i).getJSONArray("Topics");
                    for (int j = 0; j < topics.length(); j++) {
                        JSONObject topic = topics.getJSONObject(j);
                        System.out.println(topic.get("Title"));
                    }
                }
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();

                /**
                 * This covers Dropbox Folders
                 *
                 * System.out.println(ouList1);
                 * driver.get("https://byui.brightspace.com/d2l/lms/dropbox/admin/folders_manage.d2l?ou="
                 * + ouList1); driver.manage().timeouts().implicitlyWait(3,
                 * TimeUnit.SECONDS);
                 *
                 * List<WebElement> links =
                 * driver.findElements(By.xpath("//a[contains(@href,
                 * '/d2l/lms/dropbox/admin/mark/')]"));
                 *
                 * ArrayList<String> dropBoxes = new ArrayList<>();
                 *
                 * System.out.println(links.size()); for (WebElement link :
                 * links) {
                 *
                 * System.out.println(link.getAttribute("href")); if
                 * (link.getAttribute("href") != null &&
                 * link.getAttribute("href").contains("/d2l/lms/dropbox/admin/mark/"))
                 * {
                 * dropBoxes.add(link.getAttribute("href").replace("mark/folder_submissions_users",
                 * "modify/folder_newedit_properties"));
                 * System.out.println("successfully pulled: " +
                 * link.getAttribute("href")); } }
                 *
                 * for (int j = 0; j < dropBoxes.size(); j++) { String dropBox =
                 * dropBoxes.get(j); driver.get(dropBox);
                 *
                 * if (!driver.findElements(By.linkText("Show Submission
                 * Options")).isEmpty()) { driver.findElement(By.linkText("Show
                 * Submission Options")).click();
                 * driver.manage().timeouts().implicitlyWait(1800,
                 * TimeUnit.SECONDS); }
                 *
                 * if (driver.findElement(By.id("z_cd")).isSelected()) {
                 * //((JavascriptExecutor)
                 * driver).executeScript("arguments[0].scrollIntoView(true);",
                 * driver.findElement(By.id("z_ce")));
                 * actions.moveToElement(driver.findElement(By.id("z_ce"))).click().perform();
                 * actions.moveToElement(driver.findElement(By.id("z_ci"))).click().perform();
                 * driver.findElement(By.id("z_c")).click();
                 * driver.manage().timeouts().implicitlyWait(3,
                 * TimeUnit.SECONDS); } }
                 *
                 * // Response response = json.fromJson(, Response.class) /**
                 * This covers content.
                 */
                /*
                driver.get("https://byui.brightspace.com/d2l/le/content/9730/Home");
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                List<WebElement> dragElement = driver.findElements(By.xpath("//div[contains(@id,'TreeItem')]//div[contains(@class, 'd2l-textblock')]"));
                /*
                for (int i = 4; i < dragElement.size(); i++) {
                
                WebElement drag = dragElement.get(i);
                
                
                drag.click();
                
                wait.until(ExpectedConditions.elementToBeClickable(drag));
                /*
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                System.out.println(driver.findElement(By.xpath("//h1[contains(@class, 'd2l-page-title d2l-heading vui-heading-1')]")).getText());
                (new WebDriverWait(driver, 60)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                return drag.getText().contains(d.findElement(By.xpath("//h1[contains(@class, 'd2l-page-title d2l-heading vui-heading-1')]")).getText());
                }
                });
                
                
                try {
                // while the following loop runs, the DOM changes -
                // page is refreshed, or element is removed and re-added
                // This took me forever to figure out!!!
                Thread.sleep(2000);
                } catch (InterruptedException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                List<WebElement> contentItems = driver.findElements(By.className("d2l-fuzzydate"));

                for (int k = 1; k < contentItems.size(); k++) {
                WebElement content = contentItems.get(k);
                wait.until(presenceOfElementLocated(By.className(content.getAttribute("class"))));
                WebElement parent1 = content.findElement(By.xpath(".."));
                System.out.println(parent1.getTagName());
                WebElement parent2 = parent1.findElement(By.xpath(".."));
                System.out.println(parent2.getTagName());
                WebElement parent3 = parent2.findElement(By.xpath(".."));
                System.out.println(parent3.getTagName());
                WebElement parent4 = parent3.findElement(By.xpath(".."));
                System.out.println(parent4.getTagName());
                WebElement parent5 = parent4.findElement(By.xpath(".."));
                System.out.println(parent5.getTagName());
                WebElement parent6 = parent5.findElement(By.xpath(".."));
                System.out.println(parent6.getTagName());
                //System.out.println(parent5.getText());
                System.out.println(parent6.getAttribute("title"));
                }
                
                }
                 */
                /**
                 * This covers quizzes
                 */
                /*
                driver.get("https://byui.brightspace.com/d2l/lms/quizzing/admin/quizzes_manage.d2l?ou=" + ouList1);
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                List<WebElement> links = driver.findElements(By.className("vui-outline"));
                
                ArrayList<String> quizzes = new ArrayList<>();
                
                for (WebElement link : links) {
                if (link.getAttribute("href") != null && link.getAttribute("href").contains("byui.brightspace.com/d2l/lms/quizzing/admin/modify")) {
                quizzes.add(link.getAttribute("href"));
                System.out.println("successfully pulled: " + link.getAttribute("href"));
                
                }
                }
                
                for (int j = 0; j < quizzes.size(); j++) {
                String quiz = quizzes.get(j);
                boolean isLA = false;
                driver.get(quiz);
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                if (!driver.findElements(By.linkText("Expand optional advanced properties")).isEmpty()) {
                driver.findElement(By.linkText("Expand optional advanced properties")).click();
                driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                
                }

                if (driver.findElement(By.name("disableRightClick")).isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.name("disableRightClick")));
                driver.findElement(By.name("disableRightClick")).click();
                driver.findElement(By.id("z_b")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                count++;
                }

                List<WebElement> labels = driver.findElements(By.tagName("label"));
                for (WebElement label : labels) {
                if (label.getText().contains("LA")) {
                isLA = true;
                break;
                }
                }
                
                driver.findElement(By.id("z_h_Assessment_l")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                if (driver.findElement(By.name("autoExportGrades")).isSelected()
                && isLA == true) {
                driver.findElement(By.name("autoExportGrades")).click();
                count++;
                }

                if (driver.findElement(By.name("autoSetGraded")).isSelected()
                && isLA == true) {
                driver.findElement(By.name("autoSetGraded")).click();
                count++;
                }

                if (!driver.findElement(By.name("autoSetGraded")).isSelected()
                && isLA == false) {
                driver.findElement(By.name("autoSetGraded")).click();
                count++;
                
                }

                if (!driver.findElement(By.name("autoExportGrades")).isSelected()
                && isLA == false) {
                driver.findElement(By.name("autoExportGrades")).click();
                count++;
                }

                driver.findElement(By.id("z_b")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                System.out.println("count is: " + count);

                /**
                *
                * Submission Views
                *
                 */
 /*
                driver.findElement(By.linkText("Submission Views")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                driver.findElement(By.linkText("Default View")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                if (!driver.findElement(By.name("showQuestions")).isSelected()) {
                System.out.println("show answers clicked!!!  URL: " + quiz);
                driver.findElement(By.name("showQuestions")).click();
                }
                
                if (!driver.findElement(By.id("z_p")).isSelected()) {
                driver.findElement(By.id("z_p")).click();
                }
                if (!driver.findElement(By.name("showCorrectAnswers")).isSelected()) {
                driver.findElement(By.name("showCorrectAnswers")).click();
                }
                if (!driver.findElement(By.name("showQuestionScore")).isSelected()) {
                driver.findElement(By.name("showQuestionScore")).click();
                }
                if (!driver.findElement(By.name("showScore")).isSelected()) {
                driver.findElement(By.name("showScore")).click();
                }
                
                driver.findElement(By.id("z_a")).click();
                 */
                //}
                /**
                 * This covers content.
                 */
                /*
                driver.get("https://byui.brightspace.com/d2l/le/content/9730/Home");
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                List<WebElement> dragElement = driver.findElements(By.xpath("//div[contains(@id,'TreeItem')]//div[contains(@class, 'd2l-textblock')]"));
                /*
                for (int i = 4; i < dragElement.size(); i++) {
                
                WebElement drag = dragElement.get(i);
                
                
                drag.click();
                
                wait.until(ExpectedConditions.elementToBeClickable(drag));
                /*
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                System.out.println(driver.findElement(By.xpath("//h1[contains(@class, 'd2l-page-title d2l-heading vui-heading-1')]")).getText());
                (new WebDriverWait(driver, 60)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                return drag.getText().contains(d.findElement(By.xpath("//h1[contains(@class, 'd2l-page-title d2l-heading vui-heading-1')]")).getText());
                }
                });
                
                
                try {
                // while the following loop runs, the DOM changes -
                // page is refreshed, or element is removed and re-added
                // This took me forever to figure out!!!
                Thread.sleep(2000);
                } catch (InterruptedException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                List<WebElement> contentItems = driver.findElements(By.className("d2l-fuzzydate"));
                
                for (int k = 1; k < contentItems.size(); k++) {
                WebElement content = contentItems.get(k);
                wait.until(presenceOfElementLocated(By.className(content.getAttribute("class"))));
                WebElement parent1 = content.findElement(By.xpath(".."));
                System.out.println(parent1.getTagName());
                WebElement parent2 = parent1.findElement(By.xpath(".."));
                System.out.println(parent2.getTagName());
                WebElement parent3 = parent2.findElement(By.xpath(".."));
                System.out.println(parent3.getTagName());
                WebElement parent4 = parent3.findElement(By.xpath(".."));
                System.out.println(parent4.getTagName());
                WebElement parent5 = parent4.findElement(By.xpath(".."));
                System.out.println(parent5.getTagName());
                WebElement parent6 = parent5.findElement(By.xpath(".."));
                System.out.println(parent6.getTagName());
                //System.out.println(parent5.getText());
                System.out.println(parent6.getAttribute("title"));
                }
                
                }
                 */
                /**
                 * This covers quizzes
                 */
                /*
            driver.get("https://byui.brightspace.com/d2l/lms/quizzing/admin/quizzes_manage.d2l?ou=" + ouList1);
            driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
            System.out.println("Opening OU# " + ouList1);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("d2l-tool-areas")));

            List<WebElement> links = driver.findElements(By.xpath("//a[contains(@href,'/d2l/lms/quizzing/admin/modify/quiz_newedit_properties.d2l?qi=')]"));
            System.out.println("viu outline obtained");

            ArrayList<String> quizzes = new ArrayList<>();
            
            System.out.println(links.size());

            for (WebElement link : links) {
                if (link.getAttribute("href") != null && link.getAttribute("href").contains("byui.brightspace.com/d2l/lms/quizzing/admin/modify")) {
                    quizzes.add(link.getAttribute("href"));
                    System.out.println("successfully pulled: " + link.getAttribute("href"));
                }
            }
            System.out.println(quizzes.size());

            for (int j = 0; j < quizzes.size(); j++) {
                String quiz = quizzes.get(j);
                boolean isLA = false;
                driver.get(quiz);
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                if (!driver.findElements(By.linkText("Expand optional advanced properties")).isEmpty()) {
                    driver.findElement(By.linkText("Expand optional advanced properties")).click();
                    driver.manage().timeouts().implicitlyWait(1800, TimeUnit.SECONDS);

                }

                wait.until(ExpectedConditions.elementToBeClickable(By.name("disableRightClick")));
                if (driver.findElement(By.name("disableRightClick")).isSelected()) {
                    driver.findElement(By.name("disableRightClick")).click();
                    driver.findElement(By.id("z_b")).click();
                    driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                    count++;
                }

                List<WebElement> longAnswer = driver.findElements(By.xpath("//label[contains(.,'LA')]"));
                if (longAnswer.size() > 0) {
                    isLA = true;
                }

                quiz = quiz.replace("/quiz_newedit_properties", "/quiz_newedit_assessment");
                driver.get(quiz);
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name("autoExportGrades")));
                if (driver.findElement(By.name("autoExportGrades")).isSelected()
                        && isLA == true) {
                    driver.findElement(By.name("autoExportGrades")).click();
                    count++;
                }

                wait.until(ExpectedConditions.elementToBeClickable(By.name("autoSetGraded")));
                if (driver.findElement(By.name("autoSetGraded")).isSelected()
                        && isLA == true) {
                    driver.findElement(By.name("autoSetGraded")).click();
                    count++;
                }

                wait.until(ExpectedConditions.elementToBeClickable(By.name("autoSetGraded")));
                if (!driver.findElement(By.name("autoSetGraded")).isSelected()
                        && isLA == false) {
                    driver.findElement(By.name("autoSetGraded")).click();
                    count++;

                }

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name("autoExportGrades")));
                if (!driver.findElement(By.name("autoExportGrades")).isSelected()
                        && isLA == false) {
                    driver.findElement(By.name("autoExportGrades")).click();
                    count++;
                }

                wait.until(ExpectedConditions.elementToBeClickable(By.id("z_b")));
                driver.findElement(By.id("z_b")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);

                System.out.println("count is: " + count);

                /**
                 *
                 * Submission Views
                 *
                 */
 /*
                driver.findElement(By.linkText("Submission Views")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                driver.findElement(By.linkText("Default View")).click();
                driver.manage().timeouts().pageLoadTimeout(1800, TimeUnit.SECONDS);
                
                if (!driver.findElement(By.name("showQuestions")).isSelected()) {
                System.out.println("show answers clicked!!!  URL: " + quiz);
                driver.findElement(By.name("showQuestions")).click();
                }
                
                if (!driver.findElement(By.id("z_p")).isSelected()) {
                driver.findElement(By.id("z_p")).click();
                }
                if (!driver.findElement(By.name("showCorrectAnswers")).isSelected()) {
                driver.findElement(By.name("showCorrectAnswers")).click();
                }
                if (!driver.findElement(By.name("showQuestionScore")).isSelected()) {
                driver.findElement(By.name("showQuestionScore")).click();
                }
                if (!driver.findElement(By.name("showScore")).isSelected()) {
                driver.findElement(By.name("showScore")).click();
                }
                
                driver.findElement(By.id("z_a")).click();
                 */
                //}
                /**
                 * End of FOR LOOP stub
                 */
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

        }

    }



private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
