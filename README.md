# memo_app_challenge
프로그래머스 라인 Android 앱 과제

## 개발 정보
- 언어 : Java
- 개발툴 : Android studio
- 사용 라이브러리 : Picasso (이미지 불러오기)
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

# 기능 설명
## 메인 화면
<br>
1. 툴바 - 일반 모드(추가, 삭제), 삭제 모드(전체 선택, 삭제, 취소)<br>
일반 모드 > 추가 : 메모 추가 화면으로 전환<br>
일반 모드 > 삭제 : 메모 삭제 모드로 전환, 메모 리스트에 체크박스 추가 (메모 아이템이 없다면 전환 X)<br>
<br>
삭제 모드 > 전체 선택 : 메모 리스트 전체 선택<br>
삭제 모드 > 삭제 : 리스트 중 선택한 메모 삭제<br>
삭제 모드 > 취소 : 일반 모드로 전환<br>
<br>
2. 메모 아이템 - 클릭, 롱클릭<br>
클릭 : 메모 내용 보기 화면으로 전환<br>
롱클릭 : 편집, 삭제<br>
롱클릭 > 편집 : 메모 내용 보기 화면으로 전환<br>
롱클릭 > 삭제 : 리스트 중 선택한 메모 삭제<br>
<br>
3. 플로팅 버튼 - 메모 추가 화면으로 전환<br>
<br>
<br>
## 메모 추가 화면
1. 툴바 - 저장 : 메모 저장 후 메인 화면으로 전환<br>
<br>
2. 이미지 추가하기 버튼 : 갤러리에서 이미지 추가, 카메라로 이미지 추가, URL로 사진 추가<br>
갤러리에서 이미지 추가 : 휴대폰의 갤러리로 이동 후 사진 선택 -> 이미지 추가<br>
카메라로 이미지 추가 : 휴대폰의 카메라로 이동 후 사진 촬영 -> 이미지 미리보기 화면 확인 선택 -> 이미지 추가<br>
URL로 사진 추가 : URL입력 팝업 생성 -> 유효한 URL(https://) 입력 -> 이미지 추가 (유효하지 않을 시 TextView 생성)<br>
<br>
<br>
## 메모 내용 보기 화면
<br>
0. 기본 기능<br>
일반 모드 > 제목, 내용 클릭 : 편집 모드 전환 (TextView -> EditText)<br>
일반 모드 > 이미지 아이템 : 이미지 보기 화면으로 전환<br>
<br>
1. 툴바 - 일반 모드(편집, 뒤로가기), 편집 모드(저장, 뒤로가기)<br>
일반 모드 > 편집 : 편집 모드로 전환<br>
일반 모드 > 뒤로가기 : 메인 화면으로 전환<br>
<br>
편집 모드 > 저장 : 메모 저장 후 메인 화면으로 전환<br>
편집 모드 > 뒤로가기 : 메모 저장 여부 팝업 생성(저장 -> 메모 저장 후 메인 화면 이동, 취소 -> 메인 화면으로 이동)<br>
<br>
2. 이미지 추가하기 버튼 : (메모 추가 화면의 기능과 동일)<br>
<br>
