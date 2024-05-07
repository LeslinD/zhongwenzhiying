<script>
  let dark = false

  function changeTheme() {
    document.documentElement.classList.toggle('dark')
    dark = !dark
  }
</script>

<button 
  on:click={changeTheme}
  class="w-full px-2 py-2">
  {#if dark}
    &#9728;
  {:else}
    &#127770;
  {/if}
</button>