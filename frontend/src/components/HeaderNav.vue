<template>
  <div>
    <b-navbar toggleable="lg" type="dark" variant="info">
      <b-navbar-brand @click="movePage('/')">
        <img id="logo" src="@/assets/logo.png" />
        SSAFY CAFE
      </b-navbar-brand>

      <b-navbar-nav class="ml-auto" v-show="!loginUser.id">
        <b-button size="sm" type="button" variant="primary" @click="movePage('/login')">
          로그인</b-button
        >
        <b-button size="sm" type="button" variant="outline-primary" @click="movePage('/register')">
          회원가입</b-button
        >
      </b-navbar-nav>
      <b-navbar-nav class="ml-auto" v-show="loginUser.id">
        <b-button size="sm" type="button" variant="primary" @click="movePage('/user-info')">
          {{ loginUser.name }}</b-button
        >
        <b-button size="sm" type="button" variant="outline-primary" @click="logout">
          로그아웃</b-button
        >
      </b-navbar-nav>
    </b-navbar>
  </div>
</template>

<script>
export default {
  name: "header-nav",
  methods: {
    movePage(url) {
      this.$router.push(url);
    },
    logout() {
      this.$store.dispatch("resetLoginUser");
      this.movePage("/");
    },
  },
  computed: {
    loginUser() {
      let loginUser = this.$store.getters.getLoginUser;
      return loginUser;
    },
  },
};
</script>

<style scope>
#logo {
  height: 30px;
}
</style>
