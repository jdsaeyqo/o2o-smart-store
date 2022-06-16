<template>
  <b-container id="container">
    <h3 id="title">상품 평점</h3>
    <div id="info">
      <img id="photo" :src="require('@/assets/menu/' + this.comm[0].img)" />
      <b-list-group>
        <b-list-group-item id="item">상품명 : {{ this.comm[0].name }}</b-list-group-item>
        <b-list-group-item id="item">상품단가 : {{ this.comm[0].price }}</b-list-group-item>
        <b-list-group-item id="item">총주문수량 : {{ sell() }}</b-list-group-item>
        <b-button @click="makeComment" id="btn">상품평 등록</b-button>
      </b-list-group>
    </div>
    <div id="reviewList">상품평 목록</div>
    <div v-if="this.comm.length">
      <table id="comment-list">
        <colgroup>
          <col style="width: 5%" />
          <col style="width: 10%" />
          <col style="width: 40%" />
          <col style="width: 10%" />
        </colgroup>
        <thead>
          <tr>
            <th>사용자</th>
            <th>평점</th>
            <th>한줄평</th>
            <th>비고</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(comment, index) in this.comm" :key="index">
            <td>{{ comment.userName }}</td>
            <td>{{ comment.rating }}</td>
            <td>{{ comment.comment }}</td>
            <td>
              <b-button
                variant="primary"
                @click="modifyComment(comment.commentId)"
                v-show="check(comment.user_id)"
                >수정</b-button
              >
              <b-button
                variant="danger"
                @click="deleteComment(comment.commentId)"
                v-show="check(comment.user_id)"
                >삭제</b-button
              >
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "product-detail-view",
  data() {
    return {
      pid: 0,
      newComment: {
        id: 0,
        userId: "",
        productId: "",
        rating: 0.0,
        comment: "",
      },
      comments: [],
    };
  },
  created() {
    const params = new URL(document.location).searchParams;
    const id = params.get("id");
    console.log(id);
    this.pid = id;
    this.$store.dispatch("selectComment", id);
    this.newComment.productId = id;

    for (let c of this.comm) {
      this.comments.push(c);
    }
  },
  computed: {
    products() {
      return this.$store.getters.getProducts;
    },
    comm() {
      return this.$store.getters.getComments;
    },
    loginUser() {
      return this.$store.getters.getLoginUser;
    },
  },
  methods: {
    check(userId) {
      if (this.loginUser.id == userId) {
        return true;
      } else {
        return false;
      }
    },
    sell() {
      let sell = 0;
      for (let o of this.comments) {
        sell += o.sells;
      }
      return sell;
    },
    makeComment() {
      let r = prompt("평점");
      let c = prompt("한줄평");
      this.newComment.id = 0;
      this.newComment.userId = this.loginUser.id;
      this.newComment.rating = r;
      this.newComment.comment = c;
      let router = this.$router;
      console.log(this.newComment);
      this.$store.dispatch("insertComment", {
        data: this.newComment,
        callback: function () {
          alert("등록이 완료 됐습니다");
          router.push({ name: "product-list-view" });
        },
      });
    },
    modifyComment(commentId) {
      let r = prompt("평점");
      let c = prompt("한줄평");
      let router = this.$router;
      this.newComment.id = commentId;
      this.newComment.userId = this.loginUser.id;
      this.newComment.rating = r;
      this.newComment.comment = c;

      console.log(this.newComment);
      this.$store.dispatch("upComment", {
        data: this.newComment,
        callback: function () {
          alert("수정이 완료 됐습니다");
          router.push({ name: "product-list-view" });
        },
      });
    },
    deleteComment(commentId) {
      let router = this.$router;
      this.$store.dispatch("delComment", {
        data: commentId,
        callback: function () {
          alert("삭제가 완료 됐습니다");
          router.push({ name: "product-list-view" });
        },
      });
    },
  },
};
</script>

<style scoped>
#comment-list {
  width: 100%;
  border: 1px solid #444444;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #444444;
  text-align: center;
}

#title {
  margin-top: 30px;
  text-align: center;
}
#info {
  display: flex;
  justify-content: center;
}

#item {
  width: 500px;
}

#btn {
  margin-top: 30px;
}

#reviewList {
  font-size: 20px;
}
</style>
