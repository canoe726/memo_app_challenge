# memo_app_challenge
프로그래머스 라인 Android 앱 과제

## 개발 정보
- 언어 : Java
- 개발툴 : Android studio
- 사용 라이브러리 : Picasso (이미지 불러오기), 링크 : https://github.com/square/picasso
- Sdk 정보 : minSdkVersion: 21, targetSdkVersion: 29

## 앱 화면 스크린샷
<div>
<img width="200" src="https://user-images.githubusercontent.com/36183001/75092799-ca09ab80-55be-11ea-8c6a-be8ea0007b00.jpg">
  &nbsp;&nbsp;
<img width="200" src="https://user-images.githubusercontent.com/36183001/75092798-c9711500-55be-11ea-84ea-b8446aaa65d4.jpg">
  &nbsp;&nbsp;
<img width="200" src="https://user-images.githubusercontent.com/36183001/75092795-c83fe800-55be-11ea-9e34-3d37130e239d.jpg">
  &nbsp;&nbsp;
<img width="200" src="https://user-images.githubusercontent.com/36183001/75092783-b78f7200-55be-11ea-8e18-88228d13e896.jpg">
  &nbsp;&nbsp;
</div>
<br>

## 기능 설명
### 메인 화면
1. 툴바 - 일반 모드(추가, 삭제), 삭제 모드(전체 선택, 삭제, 취소)
- 일반 모드 > 추가 : 메모 추가 화면으로 전환
- 일반 모드 > 삭제 : 메모 삭제 모드로 전환, 메모 리스트에 체크박스 추가 (메모 아이템이 없다면 전환 X)

- 삭제 모드 > 전체 선택 : 메모 리스트 전체 선택
- 삭제 모드 > 삭제 : 리스트 중 선택한 메모 삭제
- 삭제 모드 > 취소 : 일반 모드로 전환

2. 메모 아이템 - 클릭, 롱클릭
- 클릭 : 메모 내용 보기 화면으로 전환
- 롱클릭 : 편집, 삭제
- 롱클릭 > 편집 : 메모 내용 보기 화면으로 전환
- 롱클릭 > 삭제 : 리스트 중 선택한 메모 삭제

3. 플로팅 버튼 - 메모 추가 화면으로 전환

### 메모 추가 화면
1. 툴바 - 저장 : 메모 저장 후 메인 화면으로 전환

2. 이미지 추가하기 버튼 : (갤러리에서 이미지 추가, 카메라로 이미지 추가, URL로 사진 추가)
- 갤러리에서 이미지 추가 : 휴대폰의 갤러리로 이동 후 사진 선택 -> 이미지 추가
- 카메라로 이미지 추가 : 휴대폰의 카메라로 이동 후 사진 촬영 -> 이미지 미리보기 화면 확인 선택 -> 이미지 추가
- URL로 사진 추가 : URL입력 팝업 생성 -> 유효한 URL(https://) 입력 -> 이미지 추가 (유효하지 않을 시 TextView 생성)

### 메모 내용 보기 화면
0. 기본 기능
- 일반 모드 > 제목, 내용 클릭 : 편집 모드 전환 (TextView -> EditText)
- 일반 모드 > 이미지 아이템 : 이미지 보기 화면으로 전환

1. 툴바 - 일반 모드(편집, 뒤로가기), 편집 모드(저장, 뒤로가기)
- 일반 모드 > 편집 : 편집 모드로 전환
- 일반 모드 > 뒤로가기 : 메인 화면으로 전환

- 편집 모드 > 저장 : 메모 저장 후 메인 화면으로 전환
- 편집 모드 > 뒤로가기 : 메모 저장 여부 팝업 생성(저장 -> 메모 저장 후 메인 화면 이동, 취소 -> 메인 화면으로 이동)

2. 이미지 추가하기 버튼 : (메모 추가 화면의 기능과 동일)

### 라인 앱 챌린지 피드백 아쉬운점 및 수정
- 아쉬운점
1. 데이터 클래스인 MemoInfoData가 왜 Application을 상속받고 있는지 이상합니다.
2. 데이터를 json으로 처리하고 있지만 json처리가 미숙하여 아쉽습니다.
3. 지금은 메모데이터를 앱 종료시점을 잡아서 json 파일로 쓰고 있는데, 이렇게 처리하기 보다는 sqlite 같은 앱용 db를 사용해서 저장을 하시는게 더 좋을 것 같습니다.
4. 코딩 컨벤션이 정리되어 있지 않은 점이 아쉽습니다.
5. 역할별로 클래스나 메소드를 조금 더 쪼개고 정리를 잘했으면 좋겠다는 생각이 듭니다.
6. Thread 처리 방식이 매끄럽지 못하고 예외처리가 되지 않아 아쉽습니다.

- 수정
1. 사용 이유 : MemoInfoData를 전역변수로 사용해서 json 파일로 저장하기 위해 Application을 상속받음
2. 사용 이유 : json 으로 데이터를 처리하여 앱에 저장하기 위해 JSONObject 사용
3. 사용 이유 : 앱 용 db를 사용하지 않았기 때문에 종료시점을 잡아서 파일 저장
=> 앱 용 db인 sqlite를 사용해서 데이터베이스를 생성함으로서 json 파일 형식을 사용하지 않고 데이터를 저장하고 불러오는 방식을 사용하였음
* db 구조
테이블 : MEMO_INFO
컬럼 : ID, THUMBNAIL, IMAGES, TITLE, CONTENT, DATE, CHECKED
속성 : INT PRIMARY KEY, VARCHAR, TEXT, VARCHAR NOT NULL, TEXT, VARCHAR, INTEGER
4. Java의 코딩 컨벤션 숙지 미흡
5. 객체지향 프로그래밍 미숙
=> Java의 코딩 컨벤션을 참고하여 수정
6. 예외처리, URL로 이미지 불러오기 에러시 예외처리
=> 수정중..
