package DataGenerator;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

import static DataGenerator.DataGenerator.AuthTest.makeRequest;
import static io.restassured.RestAssured.given;

public class DataGenerator {
    private DataGenerator() {
    }

    // спецификация нужна для того, чтобы переиспользовать настройки в разных запросах
    public static class AuthTest {
        private static RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();


        static void makeRequest(UserData userInfo) {
            // сам запрос
            given() // "дано"
                    .spec(requestSpec) // указываем, какую спецификацию используем
                    .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                    .when() // "когда"
                    .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                    .then() // "тогда ожидаем"
                    .statusCode(200); // код 200 OK
        }
    }

    public static class Registration {
        private static Faker faker = new Faker(new Locale("en"));

        public static UserData generateUser(String status) {
            var user = new UserData(faker.name().fullName(), faker.internet().password(), status);
            makeRequest(user);
            return user;
        }

        public static UserData generateWrongLoginUser(String status) {
            var password = faker.internet().password();
            makeRequest(new UserData(faker.name().firstName(), password, status));
            return new UserData(faker.name().firstName(), password, status);
        }

        public static UserData generateWrongPasswordUser(String status) {
            var login = faker.name().firstName();
            makeRequest(new UserData(login, faker.internet().password(), status));
            return new UserData(login, faker.internet().password(), status);
        }
    }
}

