window.addEventListener('DOMContentLoaded', function () {

    const navBtns  = document.querySelectorAll('#guide-nav > button');
    const sections = document.querySelectorAll('#guide-content > section');

    /* 탭 전환 */
    navBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const target = this.dataset.section;

            navBtns.forEach(b => b.classList.remove('active'));
            sections.forEach(s => s.classList.remove('active'));

            this.classList.add('active');
            document.getElementById(`section-${target}`).classList.add('active');
        });
    });

    /* FAQ 아코디언 */
    document.querySelectorAll('#faq-list > div').forEach(item => {
        item.querySelector('button').addEventListener('click', function () {
            item.classList.toggle('open');
        });
    });

});