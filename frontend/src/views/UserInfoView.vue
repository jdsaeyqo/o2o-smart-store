<template>
  <b-container>
    <br />
    <b-list-group>
      <b-list-group-item v-model="loginUser.name"
        >☕ 회원 이름 : {{ loginUser.name }}</b-list-group-item
      >
      <b-list-group-item v-model="loginUser.id"
        >☕ 회원 아이디 : {{ loginUser.id }}</b-list-group-item
      >
      <b-list-group-item v-model="loginUser.stamps"
        >☕ 회원 스탬프 개수 : {{ loginUser.stamps }}</b-list-group-item
      >
      <b-list-group-item v-model="grade">☕ 현재 등급 : {{ grade }}</b-list-group-item>
      <b-list-group-item v-model="nextGrade"
        >☕ 다음 단계 까지 {{ nextGrade }}개 남았습니다!</b-list-group-item
      >
    </b-list-group>
    <br />
    <div>
      <h3 class="underline">📜 주문 목록</h3>
      <div v-if="this.userOrder">
        <ul>
          <li v-for="(order, index) in this.orders" :key="index">
            <div>
              <b-button @click="getDetail(index)">{{ dateFormat(order[0].order_time) }}</b-button>
              <ul :id="'index' + index" style="display: none">
                <ul v-for="(o, idx) in order" :key="idx">
                  <img id="product_img" :src="require('@/assets/menu/' + o.img)" />
                  <li>품명 : {{ o.name }}</li>
                  <li>단가 : {{ o.price }}원,</li>
                  <li>{{ o.quantity }}잔</li>
                </ul>
              </ul>
            </div>
          </li>
        </ul>
      </div>
      <div v-else class="text-center">주문 내역이 없습니다.</div>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "user-info-view",
  data() {
    return {
      grade: "씨앗 - 1 ",
      nextGrade: 0,
      orders: [],
      userOrder: [],
    };
  },
  created() {
    this.$store.dispatch("selectProducts");

    this.checkGrade(this.loginUser.stamps);

    console.log(this.userInfo);

    for (let o of this.userInfo) {
      this.userOrder.push(o);
    }
    console.log(this.userOrder.length);

    if (this.userOrder.length > 0) {
      let oid = this.userOrder[0].o_id;

      let order = [];

      for (var i = 0; i < this.userOrder.length; i++) {
        if (this.userOrder[i].o_id == oid) {
          order.push(this.userOrder[i]);
        } else {
          this.orders.push(order);
          order = [];
          oid = this.userOrder[i].o_id;
          order.push(this.userOrder[i]);
        }

        if (i == this.userOrder.length - 1) {
          this.orders.push(order);
        }
      }

      console.log(this.orders);
    }
  },
  computed: {
    userInfo() {
      return this.$store.getters.getUserInfo;
    },
    loginUser() {
      return this.$store.getters.getLoginUser;
    },
    myOrders() {
      return this.$store.getters.getOrdersById;
    },
    detailByOid() {
      return this.$store.getters.getDetailByOid;
    },
    detailOrders() {
      return this.$store.getters.getDetails;
    },
  },
  methods: {
    dateFormat(date) {
      var formatted = date.split(".")[0].replace("T", " ");
      return formatted;
    },

    getDetail(index) {
      let d = document.getElementById("index" + index);
      if (d.style.display == "none") {
        d.style.display = "";
      } else {
        d.style.display = "none";
      }
    },
    checkGrade(stamp) {
      if (stamp < 10) {
        this.grade = "씨앗 - 1";
        this.nextGrade = 10 - this.loginUser.stamps;
      } else if (stamp < 20) {
        this.grade = "씨앗 - 2";
        this.nextGrade = 20 - this.loginUser.stamps;
      } else if (stamp < 30) {
        this.grade = "씨앗 - 3";
        this.nextGrade = 30 - this.loginUser.stamps;
      } else if (stamp < 40) {
        this.grade = "씨앗 - 4";
        this.nextGrade = 40 - this.loginUser.stamps;
      } else if (stamp < 50) {
        this.grade = "씨앗 - 5";
        this.nextGrade = 50 - this.loginUser.stamps;
      } else if (stamp < 65) {
        this.grade = "꽃 - 1";
        this.nextGrade = 65 - this.loginUser.stamps;
      } else if (stamp < 80) {
        this.grade = "꽃 - 2";
        this.nextGrade = 80 - this.loginUser.stamps;
      } else if (stamp < 95) {
        this.grade = "꽃 - 3";
        this.nextGrade = 95 - this.loginUser.stamps;
      } else if (stamp < 110) {
        this.grade = "꽃 - 4";
        this.nextGrade = 110 - this.loginUser.stamps;
      } else if (stamp < 125) {
        this.grade = "꽃 - 5";
        this.nextGrade = 125 - this.loginUser.stamps;
      } else if (stamp < 145) {
        this.grade = "열매 - 1";
        this.nextGrade = 145 - this.loginUser.stamps;
      } else if (stamp < 165) {
        this.grade = "열매 - 2";
        this.nextGrade = 165 - this.loginUser.stamps;
      } else if (stamp < 185) {
        this.grade = "열매 - 3";
        this.nextGrade = 185 - this.loginUser.stamps;
      } else if (stamp < 205) {
        this.grade = "열매 - 4";
        this.nextGrade = 205 - this.loginUser.stamps;
      } else if (stamp < 225) {
        this.grade = "열매 - 5";
        this.nextGrade = 225 - this.loginUser.stamps;
      } else if (stamp < 250) {
        this.grade = "커피콩 - 1";
        this.nextGrade = 250 - this.loginUser.stamps;
      } else if (stamp < 275) {
        this.grade = "커피콩 - 2";
        this.nextGrade = 275 - this.loginUser.stamps;
      } else if (stamp < 300) {
        this.grade = "커피콩 - 3";
        this.nextGrade = 300 - this.loginUser.stamps;
      } else if (stamp < 325) {
        this.grade = "커피콩 - 4";
        this.nextGrade = 325 - this.loginUser.stamps;
      } else if (stamp < 350) {
        this.grade = "커피콩 - 5";
        this.nextGrade = 350 - this.loginUser.stamps;
      } else {
        this.grade = "나무";
        this.nextGrade = 0;
      }
    },
  },
};
</script>
<style scope>
#totalSum {
  width: 60px;
  border: none;
}
#dropdown {
  width: 800px;
}
#dropdown-1 {
  width: 800px;
  text-align: center;
}
#dropdown-2 {
  font-weight: bold;
}
</style>
