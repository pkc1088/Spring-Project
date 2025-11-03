- **Spring05 - Form**
    - th:object : 커맨드 객체를 지정한다.
        - th:object="${item}"
    * * {...} : 선택 변수 식이라고 한다.
        * th:object 에서 선택한 객체에 접근한다
        * HTML 태그의 id , name , value 속성을 자동으로 처리해준다
        * th:field="* {itemName}" 으로 쓸 수 있다
        * ${item.itemName}와 동일하다
        * 이후 할 검증(Validation) 부분에서 큰 효과를 발휘
    * < form action="item.html" th:action th:object="${item}" method="post">
        * < input type="checkbox" id="open" th:field="* {open}" class="form-check-input">
        * item 객체의 open이란 필드 이름임
        * 타임리프의 on은 true로 취급하면 됨
    * *@ModelAttribute* 의 특별한 사용법
        * model.addAttribute(...) 을 사용해서 체크 박스를 구성하는 데이터를 반복해서 넣어야 할 때
        * @ModelAttribute("regions") public Map regions() { ~ }
        * 하면 해당 컨트롤러(FormItemController)를 요청할때 regions에서 반환한 값이 자동으로 model에 담기게 된다.
        * 파라미터에 붙던 @ModelAttribute와 다른 듯
    * th:for="${#ids.prev('regions')}"
        * 멀티 체크박스는 같은 이름의 여러 체크박스를 만들 수 있다.
        * 그런데 문제는 이렇게 반복해서 HTML 태그를 생성할 때, 생성된 HTML 태그 속성에서 name 은 같아도 되지만, id 는 모두 달라야 한다.
        * 따라서 타임리프는 이 코드를 통해 체크박스를 each 루프 안에서 반복해서 만들 때 임의로 1 , 2 , 3 숫자를 뒤에 붙여준다