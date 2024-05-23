# 🚩 핑 Ping! 2024/05/11 ~ 2024/05/24
| <img src="https://github.com/kimmandoo/Ping/assets/46841652/22f526f5-5047-4028-af6c-00df69ee422c" width="100" height="100"> |
| - |

## 기술 스택
* [x] Jetpack(Navigation, Safeargs, Datastore)
* [x] Coroutine + Flow 
* [x] DataStore
* [x] Glide 
* [x] Kotlin DSL + buildSrc
* [x] Firebase(FireStore, Authentication, CloudFunction, FCM)
* [x] 네이버 Maps API
* [x] Credential Manager - Google Login
* [x] Google App Architecture + MVVM
* [x] ChatGPT API
* [x] Retrofit2, OkHttp3
* [x] Junit⚠️(GPT API 테스트코드 하나)
---
## 스크린샷
| ![Screenshot_20240522_175052](https://github.com/kimmandoo/Ping/assets/46841652/7c7fbd6b-a6ce-40e9-b7b0-2ae7516c6c4b)|![Screenshot_20240522_170719](https://github.com/kimmandoo/Ping/assets/46841652/04ed6244-f368-494e-8e52-d53af0777126) |![Screenshot_20240520_084339](https://github.com/kimmandoo/Ping/assets/46841652/a9f42c03-4581-4131-b646-be8fa1e5131c)|
|- | -| -|
|![Screenshot_20240522_170747](https://github.com/kimmandoo/Ping/assets/46841652/a194e58c-1142-48f4-b510-a444b71ea8e6)|![Screenshot_20240522_171850](https://github.com/kimmandoo/Ping/assets/46841652/06e7f4a7-4aba-4d3c-8da5-a0f28dc0143a)| ![Screenshot_20240522_175128](https://github.com/kimmandoo/Ping/assets/46841652/8fc0adea-af7a-4cdd-b72b-393b721eb73c)|
| ![Screenshot_20240522_175431](https://github.com/kimmandoo/Ping/assets/46841652/03e61a05-9c76-47e0-9758-5073b6a85998)|![Screenshot_20240522_175525](https://github.com/kimmandoo/Ping/assets/46841652/eea0dd84-f9bd-4203-ad91-72468d9a73d6)|![Screenshot_20240522_175531](https://github.com/kimmandoo/Ping/assets/46841652/bc220e22-1a0e-4f8d-a74f-c3caa9fe8dd9)|

## Junit 사용된 테스트 코드 한 개
```kotlin
@Test
fun ChatGPTTest() = runTest {
    val repo = ChatGPTRepoImpl.initialize()
    val messages = listOf(
        Message(role = "user", content = "오늘 뭐하면 좋을까?")
    )
    runCatching {
        repo.getChatCompletion(messages)
    }.onSuccess {
        println("API 호출 성공: ${it.choices}")
        assertTrue("API 호출이 성공했습니다.", it.choices.isNotEmpty())
    }.onFailure {
        println("API 호출 실패: ${it.message}")
        assertFalse("API 호출이 실패했습니다.", true)
    }
}
```

## 시연 영상 - 유튜브 링크
[![Video Label](http://img.youtube.com/vi/KJpjHzi_CV0/0.jpg)](https://youtu.be/KJpjHzi_CV0)

## 프로젝트 구조도
|![image](https://github.com/kimmandoo/Ping/assets/46841652/ec60c007-9390-4dcb-bdb1-692cfa9a5654)|
|-|
| ![Ping 발표자료 (1)](https://github.com/kimmandoo/Ping/assets/46841652/332e0c82-28a4-4987-88a5-bed1e13efcc7)|

---
### commit convention
- feat : 기능 (새로운 기능)
- fix : 버그 (버그 수정)
- refactor : 리팩토링
- design : CSS 등 사용자 UI 디자인 변경
- comment : 필요한 주석 추가 및 변경
- style : 스타일 (코드 형식, 세미콜론 추가: 비즈니스 로직에 변경 없음)
- docs : 문서 수정 (문서 추가, 수정, 삭제, README)
- test : 테스트 (테스트 코드 추가, 수정, 삭제: 비즈니스 로직에 변경 없음)
- chore : 기타 변경사항 (빌드 스크립트 수정, assets, 패키지 매니저 등)
- init : 초기 생성
- rename : 파일 혹은 폴더명을 수정하거나 옮기는 작업만 한 경우
- remove : 파일을 삭제하는 작업만 수행한 경우
