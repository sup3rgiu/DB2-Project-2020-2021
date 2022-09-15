(function() { // avoid variables ending up in the global scope

  // page components
  let questionnaire,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
    pageOrchestrator.start(); // initialize the components
    pageOrchestrator.refresh(); // display initial content
  }, false);


  // Constructors of view components

  function Questionnaire(_questionnaire_form) {
    this.questionnaire_form = _questionnaire_form;
    this.currentTab = 0;

    let previous_button = document.getElementById("prevBtn");
    let next_button = document.getElementById("nextBtn");
    let cancel_button = document.getElementById("cancelBtn");

    next_button.addEventListener('click', (e) => {
      this.nextPrev(1)
      next_button.blur()
    });

    previous_button.addEventListener('click', (e) => {
      this.nextPrev(-1)
      previous_button.blur()
    });

    this.showTab = function(n) {
      // This function will display the specified tab of the form ...
      let x = document.getElementsByClassName("tab");
      x[n].style.display = "block";
      // ... and fix the Previous/Next buttons:
      if (n === 0) {
        previous_button.style.display = "none";
        cancel_button.style.display = "none";
      } else {
        previous_button.style.display = "inline";
      }
      if (n === (x.length - 1)) {
        next_button.classList.add('btn-success');
        next_button.classList.remove('btn-primary');
        next_button.innerHTML = "Submit";
        cancel_button.style.display = "inline";
      } else {
        next_button.classList.add('btn-primary');
        next_button.classList.remove('btn-success');
        next_button.innerHTML = "Next &raquo;";
      }
    }

    this.nextPrev = function(n) {
      let self = this;
      // This function will figure out which tab to display
      let x = document.getElementsByClassName("tab");
      // Exit the function if any field in the current tab is invalid:
      if (n === 1 && !self.validateForm()) {
        self.questionnaire_form.reportValidity()
        return false;
      }
      // Hide the current tab:
      x[self.currentTab].style.display = "none";
      // Increase or decrease the current tab by 1:
      self.currentTab = self.currentTab + n;
      // if you have reached the end of the form... :
      if (self.currentTab >= x.length) {
        //...the form gets submitted:
        self.questionnaire_form.submit();
        return false;
      }
      // Otherwise, display the correct tab:
      self.showTab(self.currentTab);
    }

    this.validateForm = function() {
      let self = this;
      return self.questionnaire_form.checkValidity();
    }

    this.reset = function() {
      this.currentTab = 0;
    }

  }

  function PageOrchestrator() {

    this.start = function() {
      questionnaire = new Questionnaire(document.getElementById("questionnaire_form"))
    };

    this.refresh = function() {
      questionnaire.reset();
      questionnaire.showTab(0);
    };
  }

})();
