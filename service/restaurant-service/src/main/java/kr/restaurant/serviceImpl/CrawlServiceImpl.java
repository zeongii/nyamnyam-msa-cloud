package kr.restaurant.serviceImpl;

import kr.restaurant.entity.RestaurantEntity;
import kr.restaurant.repository.RestaurantRepository;
import kr.restaurant.service.CrawlService;
import kr.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;
    private static final int NUM_THREADS = 4; // 사용할 스레드 수

    @Override
    public void crawlAndSaveInfos() {
        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<List<RestaurantEntity>>> futures = new ArrayList<>();
        List<String> existingNames = restaurantRepository.findAllNames();

        try {
            // 브라우저 설정
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");

            // 크롤링할 페이지 URLs
            List<String> pageUrls = getPageUrls();

            for (String url : pageUrls) {
                Future<List<RestaurantEntity>> future = executorService.submit(() -> crawlPage(url, existingNames, chromeOptions));
                futures.add(future);
            }

            List<RestaurantEntity> allRestaurants = new ArrayList<>();
            for (Future<List<RestaurantEntity>> future : futures) {
                allRestaurants.addAll(future.get());
            }

            restaurantRepository.saveAll(allRestaurants);
            System.out.println("크롤링된 정보: " + allRestaurants);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private List<String> getPageUrls() {
        List<String> urls = new ArrayList<>();
        String[] regions = {"동대문", "광화문", "마포", "동대문"};

        for (String region : regions) {
            urls.add("https://map.naver.com/v5/search/" + region + " 맛집");
        }
        return urls;
    }

    private List<RestaurantEntity> crawlPage(String url, List<String> existingNames, ChromeOptions chromeOptions) {
        List<RestaurantEntity> crawledList = new ArrayList<>();
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));

        try {
            webDriver.get(url);

            // 결과가 로드될 때까지 대기
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("searchIframe")));

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".place_bluelink")));
            List<WebElement> titleElements = webDriver.findElements(By.cssSelector(".place_bluelink"));

            int maxItemsToProcess = 10; // 각 zone 당 최대 개수
            int count = 0;
            for (int i = 0; i < titleElements.size() && count < maxItemsToProcess; i++) {
                WebElement titleElement = titleElements.get(i);

                // 스크롤하여 요소가 화면에 보이도록 함
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", titleElement);

                // 검색 결과 클릭
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", titleElement);
                wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

                // 상세보기로 프레임으로 이동
                webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
                //wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("entryIframe")));
                webDriver.switchTo().defaultContent();
                webDriver.switchTo().frame("entryIframe");


                // 이미지
                List<WebElement> imageElements = webDriver.findElements(By.cssSelector(".place_thumb.QX0J7 img"));
                String thumbnailImageUrl = imageElements.size() > 0 ? imageElements.get(0).getAttribute("src") : null;
                String secondaryImageUrl = imageElements.size() > 1 ? imageElements.get(1).getAttribute("src") : null;

                // 가게이름, 종류
                WebElement nameElement = webDriver.findElement(By.cssSelector(".GHAhO"));
                WebElement typeElement = webDriver.findElement(By.cssSelector(".lnJFt"));

                // 가게번호
                String telText = getTextSafely(webDriver, By.cssSelector(".xlx7Q"), "번호가 존재하지 않습니다.");

                // 평점
                Double rating = getRatingSafely(webDriver, wait);

                // 주소
                String address = getAddressSafely(webDriver, wait);

                // 운영시간
                String combinedOperation = getOperationTimesSafely(webDriver, wait);

                // 메뉴
                String combinedMenu = getMenuSafely(webDriver, wait);

                String nameText = nameElement.getText();
                String typeText = typeElement.getText();

                if (!existingNames.contains(nameText)) {
                    RestaurantEntity restaurant = RestaurantEntity.builder()
                            .name(nameText)
                            .type(typeText)
                            .address(address)
                            .rate(rating)
                            .operation(combinedOperation)
                            .tel(telText)
                            .menu(combinedMenu)
                            .thumbnailImageUrl(thumbnailImageUrl)
                            .subImageUrl(secondaryImageUrl)

                            .build();

                    crawledList.add(restaurant);
                    count++;
                }

                webDriver.switchTo().defaultContent();
                webDriver.switchTo().frame("searchIframe");
                titleElements = webDriver.findElements(By.cssSelector(".place_bluelink"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

        return crawledList;
    }

    private String getTextSafely(WebDriver driver, By by, String defaultValue) {
        try {
            WebElement element = driver.findElement(by);
            return element.getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return defaultValue;
        }
    }

    private Double getRatingSafely(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".PXMot.LXIwF")));
            WebElement star = driver.findElement(By.cssSelector(".PXMot.LXIwF"));
            String starText = star.getText().replaceAll("[^0-9.]", "").trim();
            return starText.isEmpty() ? 0.0 : Double.parseDouble(starText);
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("평점 정보를 찾을 수 없어 기본값 0.0을 설정합니다.");
            return null;
            // return 0.0; 중에 고민중
        }
    }

    private String getAddressSafely(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement addressButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("._UCia")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addressButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addressButton);

            WebElement addressDiv = driver.findElement(By.className("Y31Sf"));
            List<WebElement> addressInfos = addressDiv.findElements(By.className("nQ7Lh"));

            String address = "";
            for (WebElement addressInfo : addressInfos) {
                WebElement addressType = addressInfo.findElement(By.tagName("span"));
                String addressDetail = addressInfo.getText().replace(addressType.getText(), "").trim();

                if (addressType.getText().equals("도로명")) {
                    address = addressDetail.replace("복사", "").trim();
                }
            }

            return address;
        } catch (Exception e) {
            return "주소 정보가 존재하지 않습니다.";
        }
    }

    private String getOperationTimesSafely(WebDriver driver, WebDriverWait wait) {
        try {
            List<WebElement> detailButtons = driver.findElements(By.cssSelector("._UCia"));

            if (detailButtons.size() >= 2) {
                WebElement operationButton = detailButtons.get(1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", operationButton);

                List<WebElement> operationTimes = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".A_cdD>.H3ua4")));
                List<WebElement> operationDays = driver.findElements(By.cssSelector(".A_cdD>.i8cJw"));

                StringBuilder operationBuilder = new StringBuilder();
                for (int j = 0; j < operationDays.size(); j++) {
                    String dayText = operationDays.get(j).getText().trim();
                    String timeText = operationTimes.get(j).getText().replace("\n", " ").trim();

                    operationBuilder.append(dayText).append(" / ").append(timeText).append("\n");
                }

                // 마지막 줄바꿈 제거
                if (operationBuilder.length() > 0) {
                    operationBuilder.setLength(operationBuilder.length() - 1);
                }

                return operationBuilder.toString();
            }
        } catch (Exception e) {
            return "운영시간 정보가 존재하지 않습니다.";
        }
        return "운영시간 정보가 존재하지 않습니다.";
    }

    private String getMenuSafely(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ipNNM")));
            List<WebElement> menuItems = driver.findElements(By.cssSelector(".ipNNM"));

            StringBuilder combinedMenu = new StringBuilder();
            for (int k = 0; k < menuItems.size(); k++) {
                String menuNameXPath = String.format("//ul/li[%d]/a/div[2]/div[1]/div/span", k + 1);
                String menuPriceXPath = String.format("//ul/li[%d]/a/div[2]/div[2]/div", k + 1);

                WebElement menuNameElement = driver.findElement(By.xpath(menuNameXPath));
                WebElement menuPriceElement = driver.findElement(By.xpath(menuPriceXPath));

                String menuName = menuNameElement.getText();
                String menuPrice = menuPriceElement.getText();

                combinedMenu.append(menuName).append("-").append(menuPrice).append(",");
            }

            // 마지막 ", " 제거
            if (combinedMenu.length() > 0) {
                combinedMenu.setLength(combinedMenu.length() - 2);
            }

            return combinedMenu.toString();
        } catch (Exception e) {
            return "메뉴정보가 존재하지 않습니다.";
        }
    }
}
