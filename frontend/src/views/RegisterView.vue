<template>
  <b-container>
    <h1>회원가입</h1>
    <b-form>
      <b-form-group :label="'아이디:'" label-for="input-1">
        <b-form-input id="input-1" v-model="form.id" type="text" required></b-form-input>
        <span class="error" v-show="isNotRegister">다른 아이디를 사용해주세요</span>
      </b-form-group>

      <b-form-group :label="'이름:'" label-for="input-2">
        <b-form-input id="input-2" v-model="form.name" type="text" required></b-form-input>
      </b-form-group>

      <b-form-group :label="'비밀번호:'" label-for="input-3">
        <b-form-input id="input-3" v-model="form.pass" type="password" required></b-form-input>
      </b-form-group>
    </b-form>

    <div>
      <b-button variant="primary" @click="register()">가입</b-button>
      <b-button variant="danger" @click="reset()">취소</b-button>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "register-view",
  data() {
    return {
      form: {
        id: "",
        name: "",
        pass: "",
        stamps: 0,
      },
      isNotRegister: false,
    };
  },
  methods: {
    register() {
      if (!this.form.id) {
        alert("아이디를 입력하세요.");
        return;
      }

      if (!this.form.name) {
        alert("이름을 입력하세요.");
        return;
      }

      if (!this.form.pass) {
        alert("비밀번호를 입력하세요.");
        return;
      }

      this.$store.dispatch("isUsed", {
        data: this.form.id,
      });

      if (this.isUsed == true) {
        this.isNotRegister = false;
        return;
      } else {
        this.isNotRegister = true;
      }

      let router = this.$router;
      this.$store.dispatch("insertUser", {
        data: this.form,
        callback: function () {
          router.push({ name: "product-list-view" });
        },
      });
      // let user = this.$store.getters.getUser;

      // console.log(user);
      // // 회원가입 된 상태이면
      // if (user.id) {
      //   this.reset();
      //   this.movePage("/");
      //   alert("회원 가입이 완료되었습니다");
      //   this.isNotRegister = false;
      // } else {
      //   this.isNotRegister = true;
      // }
    },
    reset() {
      this.form.id = "";
      this.form.name = "";
      this.form.pass = "";
    },
    movePage(url) {
      this.$router.push(url);
    },
  },
  computed: {
    isUsed() {
      return this.$store.getters.isUsed;
    },
  },
};
</script>

<style scoped>
.error {
  color: red;
}
</style>
