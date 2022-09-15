(function() { // avoid variables ending up in the global scope

  // page components
  let questionnaire,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
    pageOrchestrator.start(); // initialize the components
    pageOrchestrator.refresh(); // display initial content
  }, false);

  // Constructors of view components

  function QuestionnaireCreation(_questionnaire_form) {
    this.questionnaire_form = _questionnaire_form;
    this.questionnaire_fieldset = this.questionnaire_form.getElementsByTagName('fieldset')[0];
    let input_options = ["Text", "Textarea", "Number", "Select"];
    let current_question = 0;

    let date_selection = document.getElementById("dateSelection");
    date_selection.min = new Date().toISOString().split("T")[0]; // min = today
    date_selection.max = new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split("T")[0]; // max = today + 1 year

    let add_field_button = document.getElementById("addField");

    add_field_button.addEventListener('click', (e) => {
      this.addField()
    });

    /* HANDLE BUTTON TO ADD A QUESTION */
    this.addField = function() {
      let self = this;
      current_question++
      let id_template = "question_" + current_question.toString();
      let name_template = `question[${current_question.toString()}]`

      let heading = document.createElement("h3");
        let strong = document.createElement("strong");
        strong.innerHTML = "Question " + current_question;
        heading.appendChild(strong);

      let form_row = document.createElement("div");
        form_row.classList.add("form-row")
      let form_group_input = document.createElement("div");
        form_group_input.classList.add("form-group", "col")
      let form_group_select = document.createElement("div");
        form_group_select.classList.add("form-group", "col")


      /* CREATE QUESTION TEXT input */
      let label_question = document.createElement("label");
        label_question.setAttribute("for", id_template + "_text");
        label_question.innerHTML = "Insert the text of the question:";
      let input_question = document.createElement("input");
        input_question.type = "text";
        input_question.id = id_template + "_text"
        input_question.name = name_template + "[question]";
        input_question.placeholder = 'Question text...'
        input_question.required = true;
        input_question.classList.add("form-control")
      form_group_input.appendChild(label_question);
      form_group_input.appendChild(input_question);

      /* CREATE ANSWER TYPE select */
      let label_type = document.createElement("label");
        label_type.setAttribute("for", id_template + "_type");
        label_type.innerHTML = "Select the type of the answer:";
      let select_type = document.createElement("select");
        select_type.id = id_template + "_type";
        select_type.name = name_template + "[type]";
        select_type.classList.add("form-control")
        for (let i = 0; i < input_options.length; i++) {
          let option = document.createElement("option");
          option.value = input_options[i];
          option.text = input_options[i];
          select_type.appendChild(option);
        }
      form_group_select.appendChild(label_type);
      form_group_select.appendChild(select_type);

      let div_type = document.createElement("div");
        div_type.classList.add("pt-1");
      form_group_select.appendChild(div_type);
      form_row.appendChild(form_group_input);
      form_row.appendChild(form_group_select);

      /* HANDLE ANSWER TYPE */
      select_type.addEventListener('change', (event) => {
        while(div_type.firstChild && div_type.removeChild(div_type.firstChild)); // clear div_type (i.e. remove content)
        div_type.classList.remove("form-row");

        if(event.target.value === "Number") {         /* HANDLE NUMBER */
          div_type.classList.add("form-row")
          let min_col = document.createElement("div")
            min_col.classList.add("col")
          let max_col = min_col.cloneNode(true);

          // button to add MAX and MIN
          let button_option_group = document.createElement("button");
            button_option_group.innerHTML = "Define MAX and MIN";
            button_option_group.type = "button" // prevent submit form
            button_option_group.className = 'add';
            button_option_group.classList.add("btn", "btn-outline-secondary", "btn-sm", "mx-auto");
            button_option_group.addEventListener('click', (e) => {
              if(button_option_group.classList.contains('add')) {
                let input_option_group1 = document.createElement("input");
                  input_option_group1.type = "number";
                  input_option_group1.name = name_template + "[options]";
                  input_option_group1.placeholder = 'Min...';
                  input_option_group1.required = true;
                  input_option_group1.classList.add("form-control")
                let input_option_group2 = input_option_group1.cloneNode(true);
                  input_option_group2.placeholder = 'Max...';

                min_col.appendChild(input_option_group1);
                max_col.appendChild(input_option_group2)
                div_type.insertBefore(max_col, div_type.firstChild);
                div_type.insertBefore(min_col, div_type.firstChild);
                button_option_group.classList.remove("add")
                button_option_group.classList.add("remove")
                button_option_group.innerHTML = "Remove MAX and MIN";
              }
              else {
                let elements = div_type.getElementsByClassName("col")
                //while (elements[0]) elements[0].parentNode.removeChild(elements[0])
                while(elements[0]) {
                  while(elements[0].firstChild && elements[0].removeChild(elements[0].firstChild));
                  elements[0].parentNode.removeChild(elements[0])
                }
                button_option_group.classList.remove("remove")
                button_option_group.classList.add("add")
                button_option_group.innerHTML = "Define MAX and MIN";
              }
            });

          div_type.appendChild(button_option_group);
        }

        else if(event.target.value === "Select") {   /* HANDLE SELECT */
          let label_option_group = document.createElement("label");
            label_option_group.setAttribute("for", id_template + "_optionGroup");
            label_option_group.innerHTML = "Add the options:";

          // add two default options
          let input_option_group1 = document.createElement("input");
            input_option_group1.type = "text";
            input_option_group1.name = name_template + "[options]";
            input_option_group1.required = true;
            input_option_group1.classList.add("form-control", "mx-auto");
            input_option_group1.style.width = '70%';
          let input_option_group2 = input_option_group1.cloneNode(true);

          // button to add more options
          let button_option_group = document.createElement("button");
            button_option_group.innerHTML = "Add option";
            button_option_group.type = "button" // prevent submit form
            button_option_group.classList.add("btn", "btn-outline-primary", "btn-sm", "mt-1");
            button_option_group.addEventListener('click', (e) => {
              let input_option_group_extra = input_option_group1.cloneNode(true);
                input_option_group_extra.value = ''
              let last_option = div_type.querySelectorAll("input:last-of-type")[0]; // get last option
              insertAfter(input_option_group_extra, last_option) // add option after last previous option
            });

          // add the elements to the paragraph
          div_type.appendChild(label_option_group);
          div_type.appendChild(input_option_group1);
          div_type.appendChild(input_option_group2);
          div_type.appendChild(button_option_group);
        }
      });

      self.questionnaire_fieldset.appendChild(heading);
      self.questionnaire_fieldset.appendChild(form_row);
      self.questionnaire_fieldset.querySelector("#numberOfQuestions_input").value++;

    }

    function insertAfter(newNode, referenceNode) {
      referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
    }

    this.reset = function() {
    }

    this.start = function() {
      this.addField(); // add one question at startup
    }
  }

  function PageOrchestrator() {

    this.start = function() {
      questionnaire = new QuestionnaireCreation(document.getElementById("questionnaire_creation_form"))
      questionnaire.start();
    };

    this.refresh = function() {
      questionnaire.reset();
    };
  }

})();
